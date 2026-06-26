package com.github.exabrial.cdi.nanoscopes.testscoped.internal;

import java.lang.annotation.Annotation;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestScoped;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitScoped;
import com.github.exabrial.cdi.nanoscopes.testscoped.extension.TestScopedCdiPortableExtension;

@ApplicationScoped
public class TestScopedLifecycleController {
	private static final Logger log = LoggerFactory.getLogger(TestScopedLifecycleController.class);
	private static final ThreadLocal<Stack<String>> TEST_SCOPE_KEYS = ThreadLocal.withInitial(Stack::new);
	private static final ThreadLocal<Stack<String>> TEST_UNIT_SCOPE_KEYS = ThreadLocal.withInitial(Stack::new);
	private static final ThreadLocal<Boolean> PROPAGATED_TEST_UNIT = ThreadLocal.withInitial(() -> Boolean.FALSE);
	private static volatile Bean<?> cachedBeanDefinition;

	@Inject
	private BeanManager beanManager;
	@Inject
	private TestMetadataImpl testMetadata;
	@Inject
	private TestUnitMetadataImpl testUnitMetadata;

	@Inject
	@Initialized(TestUnitScoped.class)
	private Event<String> testUnitScopedInitialized;
	@Inject
	@Destroyed(TestUnitScoped.class)
	private Event<String> testUnitScopedDestroyed;

	@Inject
	@Initialized(TestScoped.class)
	private Event<String> testScopedInitialized;
	@Inject
	@Destroyed(TestScoped.class)
	private Event<String> testScopedDestroyed;

	private final ConcurrentHashMap<String, String> activeTestUnitScopeKeys = new ConcurrentHashMap<>();
	private final AtomicInteger counter = new AtomicInteger();

	@PostConstruct
	void postConstruct() {
		counter.set(new Random().nextInt(Integer.MAX_VALUE / 2));
	}

	public static void beforeAll(final ExtensionContext extensionContext) {
		final CDI<Object> currentCDI = CDI.current();
		final BeanManager beanManager = currentCDI.getBeanManager();
		CreationalContext<?> beanCreationalContext = null;
		try {
			beanCreationalContext = beanManager.createCreationalContext(getCached(beanManager));
			final TestScopedLifecycleController controller = (TestScopedLifecycleController) beanManager.getReference(getCached(beanManager),
					TestScopedLifecycleController.class, beanCreationalContext);
			controller.activateTestUnitScoped(extensionContext);
		} finally {
			if (beanCreationalContext != null) {
				beanCreationalContext.release();
			}
		}
	}

	public static final void beforeEach(final ExtensionContext extensionContext) {
		final CDI<Object> currentCDI = CDI.current();
		final BeanManager beanManager = currentCDI.getBeanManager();
		CreationalContext<?> beanCreationalContext = null;
		try {
			beanCreationalContext = beanManager.createCreationalContext(getCached(beanManager));
			final TestScopedLifecycleController controller = (TestScopedLifecycleController) beanManager.getReference(getCached(beanManager),
					TestScopedLifecycleController.class, beanCreationalContext);
			controller.activateTestScoped(extensionContext);
		} finally {
			if (beanCreationalContext != null) {
				beanCreationalContext.release();
			}
		}
	}

	public static final void afterEach(final ExtensionContext extensionContext) {
		final CDI<Object> currentCDI = CDI.current();
		final BeanManager beanManager = currentCDI.getBeanManager();
		CreationalContext<?> beanCreationalContext = null;
		try {
			beanCreationalContext = beanManager.createCreationalContext(getCached(beanManager));
			final TestScopedLifecycleController controller = (TestScopedLifecycleController) beanManager.getReference(getCached(beanManager),
					TestScopedLifecycleController.class, beanCreationalContext);
			controller.deactivateTestScoped(extensionContext);
		} finally {
			if (beanCreationalContext != null) {
				beanCreationalContext.release();
			}
		}
	}

	public static void afterAll(final ExtensionContext extensionContext) {
		final CDI<Object> currentCDI = CDI.current();
		final BeanManager beanManager = currentCDI.getBeanManager();
		CreationalContext<?> beanCreationalContext = null;
		try {
			beanCreationalContext = beanManager.createCreationalContext(getCached(beanManager));
			final TestScopedLifecycleController controller = (TestScopedLifecycleController) beanManager.getReference(getCached(beanManager),
					TestScopedLifecycleController.class, beanCreationalContext);
			controller.deactivateTestUnitScoped(extensionContext);
		} finally {
			if (beanCreationalContext != null) {
				beanCreationalContext.release();
			}
		}
	}

	public void activateTestUnitScoped(final ExtensionContext extensionContext) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(TestUnitScoped.class);
		final String scopeKey = newScopeKey(extensionContext, TestUnitScoped.class);
		final String previousScopeKey = context.enter(scopeKey);
		testUnitMetadata.setTestUnitName(toUnitName(extensionContext));
		testUnitMetadata.setTestUnitScopeKey(scopeKey);
		TEST_UNIT_SCOPE_KEYS.get().push(previousScopeKey);
		activeTestUnitScopeKeys.put(toUnitName(extensionContext), scopeKey);
		log.trace("activateTestUnitScoped() unitName:{} -> scopeKey:{} (previous:{})", testUnitMetadata.getTestUnitName(), scopeKey,
				previousScopeKey);
		testUnitScopedInitialized.fire(scopeKey);
	}

	public void activateTestScoped(final ExtensionContext extensionContext) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> unitContext = (ScopeContext<String>) beanManager.getContext(TestUnitScoped.class);
		final String propagatedUnitScopeKey = activeTestUnitScopeKeys.get(toUnitName(extensionContext));
		if (unitContext.getKey() == null && propagatedUnitScopeKey != null) {
			unitContext.enter(propagatedUnitScopeKey);
			PROPAGATED_TEST_UNIT.set(Boolean.TRUE);
			log.trace("activateTestScoped() propagated TestUnitScoped to thread:{} scopeKey:{}", Thread.currentThread().getName(),
					propagatedUnitScopeKey);
		}

		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(TestScoped.class);
		final String scopeKey = newScopeKey(extensionContext, TestScoped.class);
		final String previousScopeKey = context.enter(scopeKey);
		TEST_SCOPE_KEYS.get().push(previousScopeKey);
		testMetadata.setTestDisplayName(extensionContext.getDisplayName());
		testMetadata.setTestScopeKey(scopeKey);
		log.trace("activateTestScoped() unitName:{} displayName:{} -> scopeKey:{} (previous:{})", testMetadata.getTestUnitName(),
				testMetadata.getTestDisplayName(), scopeKey, previousScopeKey);
		testScopedInitialized.fire(scopeKey);
	}

	public void deactivateTestScoped(final ExtensionContext extensionContext) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(TestScoped.class);
		final String currentScopeKey = context.getKey();
		final String parentScopeKey = popAndClean(TEST_SCOPE_KEYS);
		log.trace("deactivateTestScoped() unitName:{} displayName:{} -> scopeKey:{} (restoring:{})", testMetadata.getTestUnitName(),
				testMetadata.getTestDisplayName(), currentScopeKey, parentScopeKey);
		context.destroy(currentScopeKey);
		testScopedDestroyed.fire(currentScopeKey);
		context.exit(parentScopeKey);

		if (PROPAGATED_TEST_UNIT.get()) {
			@SuppressWarnings("unchecked")
			final ScopeContext<String> unitContext = (ScopeContext<String>) beanManager.getContext(TestUnitScoped.class);
			unitContext.exit(null);
			PROPAGATED_TEST_UNIT.remove();
			log.trace("deactivateTestScoped() released propagated TestUnitScoped on thread:{}", Thread.currentThread().getName());
		}
	}

	public void deactivateTestUnitScoped(final ExtensionContext extensionContext) {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(TestUnitScoped.class);
		final String currentScopeKey = context.getKey();
		final String parentScopeKey = popAndClean(TEST_UNIT_SCOPE_KEYS);
		final String unitName = testUnitMetadata.getTestUnitName();
		log.trace("deactivateTestUnitScoped() unitName:{} -> scopeKey:{} (restoring:{})", unitName, currentScopeKey, parentScopeKey);
		context.destroy(currentScopeKey);
		testUnitScopedDestroyed.fire(currentScopeKey);
		context.exit(parentScopeKey);
		activeTestUnitScopeKeys.remove(toUnitName(extensionContext));
	}

	private String newScopeKey(final ExtensionContext extensionContext, final Class<? extends Annotation> scopeClass) {
		final String unitName = toUnitName(extensionContext);
		final String displayName = extensionContext.getDisplayName();
		final String index = Integer.toHexString(counter.getAndIncrement());
		final String newScopeKey = String.format("%s:junit5:%s:%s:%s", scopeClass.getSimpleName(), unitName, displayName, index);
		return newScopeKey;
	}

	static final Bean<?> getCached(final BeanManager beanManager) {
		if (cachedBeanDefinition == null) {
			synchronized (TestScopedCdiPortableExtension.class) {
				if (cachedBeanDefinition == null) {
					final Set<Bean<?>> beans = beanManager.getBeans(TestScopedLifecycleController.class);
					cachedBeanDefinition = beanManager.resolve(beans);
				}
			}
		}
		return cachedBeanDefinition;
	}

	static final String popAndClean(final ThreadLocal<Stack<String>> scopeKeys) {
		final String scopeKey;

		final Stack<String> stack = scopeKeys.get();
		if (stack.isEmpty()) {
			scopeKeys.remove();
			scopeKey = null;
		} else {
			scopeKey = stack.pop();
			if (stack.isEmpty()) {
				scopeKeys.remove();
			}
		}
		return scopeKey;
	}

	static final String toUnitName(final ExtensionContext extensionContext) {
		return extensionContext.getTestClass().map(Class::getSimpleName).orElse("<anon>");
	}
}
