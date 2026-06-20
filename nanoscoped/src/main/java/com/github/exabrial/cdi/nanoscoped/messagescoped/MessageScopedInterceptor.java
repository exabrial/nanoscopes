package com.github.exabrial.cdi.nanoscoped.messagescoped;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.tomitribe.microscoped.core.ScopeContext;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@MessageBoundary
@Priority(Interceptor.Priority.LIBRARY_BEFORE - 20)
public class MessageScopedInterceptor {
	private static final AtomicInteger counter = new AtomicInteger(0);
	@Inject
	private BeanManager beanManager;
	@Inject
	private Logger log;
	@Inject
	@Initialized(MessageScoped.class)
	private Event<Object> scopeInitialized;
	@Inject
	@Destroyed(MessageScoped.class)
	private Event<Object> scopeDestroyed;

	@AroundInvoke
	public Object aroundInvoke(final InvocationContext ctx) throws Exception {
		@SuppressWarnings("unchecked")
		final ScopeContext<String> context = (ScopeContext<String>) beanManager.getContext(MessageScoped.class);
		final Object result;
		if (context.getKey() != null) {
			result = ctx.proceed();
		} else {
			final String newScopeKey = ctx.getTarget().getClass().getName() + ":" + ctx.getMethod().getName() + ":"
					+ Integer.toHexString(counter.getAndIncrement());
			log.trace("aroundInvoke() entering MessageScope:{}", context);
			context.enter(newScopeKey);
			scopeInitialized.fire(newScopeKey);
			try {
				result = ctx.proceed();
			} finally {
				context.destroy(newScopeKey);
				scopeDestroyed.fire(newScopeKey);
				context.exit(null);
				log.trace("aroundInvoke() exited MessageScope:{}", context);
			}
		}
		return result;
	}
}
