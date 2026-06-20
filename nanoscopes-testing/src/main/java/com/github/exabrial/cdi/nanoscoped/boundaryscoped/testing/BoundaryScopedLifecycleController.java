package com.github.exabrial.cdi.nanonscopes.boundaryscoped.testing;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.tomitribe.microscoped.core.ScopeContext;

import com.github.exabrial.cdi.nanonscopes.boundaryscoped.BoundaryScoped;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

@ApplicationScoped
public class BoundaryScopedLifecycleController {
	private static final AtomicInteger COUNTER = new AtomicInteger(0);
	private static final ThreadLocal<String> SCOPE_KEYS = new ThreadLocal<>();

	@Inject
	private BeanManager beanManager;

	@Inject
	private Logger log;

	@Inject
	@Initialized(BoundaryScoped.class)
	private Event<Object> scopeInitialized;

	@Inject
	@Destroyed(BoundaryScoped.class)
	private Event<Object> scopeDestroyed;

	public void activate(final String unitName, final String displayName) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(BoundaryScoped.class);

		final String scopeKey = newScopeKey(unitName, displayName);
		log.trace("activate() BoundaryScope:{}", scopeKey);

		context.enter(scopeKey);
		scopeInitialized.fire(scopeKey);

		SCOPE_KEYS.set(scopeKey);
	}

	public void deactivate(final String unitName, final String displayName) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(BoundaryScoped.class);

		final String scopeKey = SCOPE_KEYS.get();
		SCOPE_KEYS.remove();

		log.trace("deactivate() BoundaryScope:{}", scopeKey);

		context.exit(null);
		context.destroy(scopeKey);
		scopeDestroyed.fire(scopeKey);
	}

	private static String newScopeKey(final String unitName, final String displayName) {
		final int seq = COUNTER.getAndIncrement();
		final int salt = ThreadLocalRandom.current().nextInt();
		return unitName + ":" + displayName + ":" + Integer.toHexString(seq ^ salt);
	}

	private static volatile Bean<?> CACHED_BEAN;

	static Bean<?> getBean(final BeanManager bm) {
		Bean<?> bean = CACHED_BEAN;
		if (bean == null) {
			synchronized (BoundaryScopedLifecycleController.class) {
				bean = CACHED_BEAN;
				if (bean == null) {
					final Set<Bean<?>> beans = bm.getBeans(BoundaryScopedLifecycleController.class);
					CACHED_BEAN = bean = bm.resolve(beans);
				}
			}
		}
		return bean;
	}

	static BoundaryScopedLifecycleController obtain(final BeanManager bm) {
		@SuppressWarnings("unchecked")
		final Bean<BoundaryScopedLifecycleController> bean = (Bean<BoundaryScopedLifecycleController>) getBean(bm);
		final CreationalContext<BoundaryScopedLifecycleController> ctx = bm.createCreationalContext(bean);
		return (BoundaryScopedLifecycleController) bm.getReference(bean, BoundaryScopedLifecycleController.class, ctx);
	}
}
