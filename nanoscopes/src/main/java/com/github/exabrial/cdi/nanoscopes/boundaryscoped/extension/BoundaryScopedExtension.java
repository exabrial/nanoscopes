package com.github.exabrial.cdi.nanoscopes.boundaryscoped.extension;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.interceptor.Boundary;
import com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.scope.BoundaryScoped;

public class BoundaryScopedExtension implements Extension {
	private static final Logger log = LoggerFactory.getLogger(BoundaryScopedExtension.class);

	void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
		log.info("beforeBeanDiscovery() Installing BoundaryScope and InterceptorBinding...");
		bbd.addScope(BoundaryScoped.class, true, false);
		bbd.addInterceptorBinding(Boundary.class);
	}

	void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		log.info("afterBeanDiscovery() Installing BoundaryScoped ScopeContext...");
		abd.addContext(new ScopeContext<>(BoundaryScoped.class));
	}
}
