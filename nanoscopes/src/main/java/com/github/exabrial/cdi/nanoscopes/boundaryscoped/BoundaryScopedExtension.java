package com.github.exabrial.cdi.nanonscopes.boundaryscoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

public class BoundaryScopedExtension implements Extension {
	private static final Logger log = LoggerFactory.getLogger(BoundaryScopedExtension.class);

	public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
		log.info("beforeBeanDiscovery() Installing BoundaryScope and InterceptorBinding...");
		bbd.addScope(BoundaryScoped.class, true, false);
		bbd.addInterceptorBinding(Boundary.class);
	}

	public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		log.info("afterBeanDiscovery() Installing BoundaryScoped ScopeContext...");
		abd.addContext(new ScopeContext<>(BoundaryScoped.class));
	}
}
