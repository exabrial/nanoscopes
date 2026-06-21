package com.github.exabrial.cdi.nanoscopes.messagescoped.testing;

import java.lang.reflect.Method;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.exabrial.cdi.nanoscopes.messagescoped.api.interceptor.MessageBoundary;

public class MessageScopedJUnit5Extension implements BeforeEachCallback, AfterEachCallback {
	@Override
	public void beforeEach(final ExtensionContext context) {
		crossBoundary(context, true);
	}

	@Override
	public void afterEach(final ExtensionContext context) {
		crossBoundary(context, false);
	}

	static final void crossBoundary(final ExtensionContext context, final boolean direction) {
		if (isMessageBoundary(context)) {
			final String unitName = context.getTestClass().map(Class::getSimpleName).orElse("<anon>");
			final String displayName = context.getDisplayName();

			final CDI<Object> current = CDI.current();
			final BeanManager bm = current.getBeanManager();

			CreationalContext<?> cc = null;
			try {
				final Bean<?> bean = MessageScopedLifecycleController.getBean(bm);
				cc = bm.createCreationalContext(bean);
				final MessageScopedLifecycleController controller = (MessageScopedLifecycleController) bm.getReference(bean,
						MessageScopedLifecycleController.class, cc);
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

	static final boolean isMessageBoundary(final ExtensionContext context) {
		return context.getTestMethod().map((final Method method) -> method.isAnnotationPresent(MessageBoundary.class)).orElse(false)
				|| context.getTestClass().map((final Class<?> clazz) -> clazz.isAnnotationPresent(MessageBoundary.class)).orElse(false);
	}
}
