package com.github.exabrial.cdi.nanoscopes.messagescoped.extension;

import jakarta.ejb.MessageDriven;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import com.github.exabrial.cdi.nanoscopes.messagescoped.api.interceptor.MessageBoundary;
import com.github.exabrial.cdi.nanoscopes.messagescoped.api.scope.MessageScoped;

public class MessageScopedExtension implements Extension {
	private static final Logger log = LoggerFactory.getLogger(MessageScopedExtension.class);

	void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
		log.info("beforeBeanDiscovery() Installing MessageScope and InterceptorBinding...");
		bbd.addScope(MessageScoped.class, true, false);
		bbd.addInterceptorBinding(MessageBoundary.class);
	}

	<T extends MessageDriven> void processAnnotatedType(@Observes final ProcessAnnotatedType<T> pat) {
		final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
		if (!annotatedType.isAnnotationPresent(MessageBoundary.class)) {
			log.info("processAnnotatedType() Adding @MessageBoundary interceptor binding to:{}",
					annotatedType.getJavaClass().getCanonicalName());
			pat.configureAnnotatedType().add(MessageBoundary.INSTANCE);
		}
	}

	void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		log.info("afterBeanDiscovery() Installing @MessageScoped ScopeContext...");
		abd.addContext(new ScopeContext<>(MessageScoped.class));
	}
}
