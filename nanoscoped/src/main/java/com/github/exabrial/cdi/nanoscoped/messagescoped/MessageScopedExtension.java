package com.github.exabrial.cdi.nanoscoped.messagescoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

public class MessageScopedExtension implements Extension {
	private static final Logger log = LoggerFactory.getLogger(MessageScopedExtension.class);

	public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
		log.info("beforeBeanDiscovery() Installing MessageScope and InterceptorBinding...");
		bbd.addScope(MessageScoped.class, true, false);
		bbd.addInterceptorBinding(MessageBoundary.class);
	}

	public <T> void processAnnotatedType(@Observes final ProcessAnnotatedType<T> pat) {
		final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
		final boolean isMessageDrivenBean = annotatedType.isAnnotationPresent(MessageDriven.class);
		if (isMessageDrivenBean && !annotatedType.isAnnotationPresent(MessageBoundary.class)) {
			log.info("processAnnotatedType() Adding @MessageBoundary interceptor binding to:{}",
					annotatedType.getJavaClass().getCanonicalName());
			pat.configureAnnotatedType().add(MessageBoundary.INSTANCE);
		}
	}

	public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		log.info("afterBeanDiscovery() Installing @MessageScoped ScopeContext...");
		abd.addContext(new ScopeContext<>(MessageScoped.class));
	}
}
