package com.github.exabrial.cdi.nanonscopes.boundaryscoped;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tomitribe.microscoped.core.ScopeContext;

import lombok.Getter;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

public class BoundaryScopedProxy<I, K extends I> implements InvocationHandler {
	private static final Set<String> BASIC_METHODS = Set.of("finalize", "wait", "notifyAll", "notify", "clone", "getClass", "hashCode",
			"equals", "toString", "getTargetType", "getTargetInterface", "getQualifiers");
	@Getter
	private final Class<I> targetInterface;
	@Getter
	private final Class<K> targetType;
	@Getter
	private final Annotation[] qualifiers;

	private final Map<String, CreationalContext<?>> boundaryScopedCreationalContextMap = new HashMap<>();
	private final Map<String, K> boundaryScopedKMap = new HashMap<>();

	public static <I, K extends I> I createProxy(final Class<I> targetInterface, final Class<K> targetType,
			final Annotation... qualifiers) {
		final I proxy = targetInterface.cast(Proxy.newProxyInstance(targetType.getClassLoader(), new Class<?>[] { targetInterface },
				new BoundaryScopedProxy<>(targetInterface, targetType, qualifiers)));
		return proxy;
	}

	private BoundaryScopedProxy(final Class<I> targetInterface, final Class<K> targetType, final Annotation... qualifiers) {
		this.targetInterface = targetInterface;
		this.targetType = targetType;
		this.qualifiers = qualifiers;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (BASIC_METHODS.contains(method.getName())) {
			return method.invoke(this, args);
		} else {
			final CDI<Object> currentCDI = CDI.current();
			final BeanManager beanManager = currentCDI.getBeanManager();
			final Bean<?> bean = beanManager.resolve(beanManager.getBeans(targetType, qualifiers));
			final InjectionPoint injectionPoint = new DynamicInjectionPoint(targetType, Set.of(qualifiers));

			if (bean.getScope() == Dependent.class) {
				final String boundaryScopeKey = getScopeKey(beanManager);
				K handler;
				synchronized (boundaryScopedCreationalContextMap) {
					handler = boundaryScopedKMap.get(boundaryScopeKey);
					if (handler == null) {
						final CreationalContext<?> beanCreationalContext = beanManager.createCreationalContext(bean);
						handler = targetType.cast(beanManager.getInjectableReference(injectionPoint, beanCreationalContext));
						boundaryScopedCreationalContextMap.put(boundaryScopeKey, beanCreationalContext);
						boundaryScopedKMap.put(boundaryScopeKey, handler);
					}
				}
				return method.invoke(handler, args);
			} else {
				CreationalContext<?> beanCreationalContext = null;
				try {
					beanCreationalContext = beanManager.createCreationalContext(bean);
					final K handler = targetType.cast(beanManager.getInjectableReference(injectionPoint, beanCreationalContext));
					return method.invoke(handler, args);
				} finally {
					if (beanCreationalContext != null) {
						beanCreationalContext.release();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getScopeKey(final BeanManager beanManager) {
		final ScopeContext<String> scopeContext = (ScopeContext<String>) beanManager.getContext(BoundaryScoped.class);
		final String currentKey = scopeContext.getKey();
		return currentKey;
	}

	public void onScopeDestroyed(final String scopeKey) {
		final CreationalContext<?> creationalContext;
		synchronized (boundaryScopedCreationalContextMap) {
			creationalContext = boundaryScopedCreationalContextMap.remove(scopeKey);
			boundaryScopedKMap.remove(scopeKey);
		}
		if (creationalContext != null) {
			creationalContext.release();
		}
	}

	@Override
	public String toString() {
		return "BoundaryScopedProxy<" + targetInterface.getSimpleName() + ", " + targetType.getSimpleName() + ">: "
				+ Arrays.toString(qualifiers) + ":" + System.identityHashCode(this);
	}

	private static class DynamicInjectionPoint implements InjectionPoint {
		private final Set<Annotation> qualifiers;
		private final Type type;

		public DynamicInjectionPoint(final Type type, final Set<Annotation> qualifiers) {
			this.type = type;
			this.qualifiers = qualifiers;
		}

		@Override
		public Type getType() {
			return type;
		}

		@Override
		public Set<Annotation> getQualifiers() {
			return qualifiers;
		}

		@Override
		public Bean<?> getBean() {
			return null;
		}

		@Override
		public Member getMember() {
			return null;
		}

		@Override
		public Annotated getAnnotated() {
			return null;
		}

		@Override
		public boolean isTransient() {
			return false;
		}

		@Override
		public boolean isDelegate() {
			return false;
		}
	}
}
