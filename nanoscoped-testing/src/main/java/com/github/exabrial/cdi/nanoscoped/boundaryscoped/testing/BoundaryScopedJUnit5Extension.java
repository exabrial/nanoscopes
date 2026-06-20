package com.github.exabrial.cdi.nanoscoped.boundaryscoped.testing;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.exabrial.cdi.nanoscoped.boundaryscoped.Boundary;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

public class BoundaryScopedJUnit5Extension implements BeforeEachCallback, AfterEachCallback {
	@Override
	public void beforeEach(final ExtensionContext context) {
		crossBoundary(context, true);
	}

	@Override
	public void afterEach(final ExtensionContext context) {
		crossBoundary(context, false);
	}

	private void crossBoundary(final ExtensionContext context, final boolean direction) {
		if (isBoundary(context)) {
			final String unitName = context.getTestClass().map(Class::getSimpleName).orElse("<anon>");
			final String displayName = context.getDisplayName();

			final CDI<Object> current = CDI.current();
			final BeanManager bm = current.getBeanManager();

			CreationalContext<?> cc = null;
			try {
				final Bean<?> bean = BoundaryScopedLifecycleController.getBean(bm);
				cc = bm.createCreationalContext(bean);
				final BoundaryScopedLifecycleController controller = (BoundaryScopedLifecycleController) bm.getReference(bean,
						BoundaryScopedLifecycleController.class, cc);
				if (direction) {
					controller.activate(unitName, displayName);
				} else {
					controller.deactivate(unitName, displayName);
				}
			} finally {
				if (cc != null) {
					cc.release();
				}
			}
		}
	}

	private boolean isBoundary(final ExtensionContext context) {
		return context.getTestMethod().map((final Method method) -> method.isAnnotationPresent(Boundary.class)).orElse(false)
				|| context.getTestClass().map((final Class<?> clazz) -> clazz.isAnnotationPresent(Boundary.class)).orElse(false);
	}
}
