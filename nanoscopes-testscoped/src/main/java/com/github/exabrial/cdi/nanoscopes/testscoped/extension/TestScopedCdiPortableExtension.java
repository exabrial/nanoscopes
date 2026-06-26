package com.github.exabrial.cdi.nanoscopes.testscoped.extension;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomitribe.microscoped.core.ScopeContext;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestScoped;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitScoped;

public class TestScopedCdiPortableExtension implements Extension {
	private static final Logger log = LoggerFactory.getLogger(TestScopedCdiPortableExtension.class);

	public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
		log.info("beforeBeanDiscovery() Installing TestScoped/TestUnitScoped scopes...");
		bbd.addScope(TestScoped.class, true, false);
		bbd.addScope(TestUnitScoped.class, true, false);
	}

	public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd) {
		log.info("afterBeanDiscovery() Installing TestScoped/TestUnitScoped contexts...");
		abd.addContext(new ScopeContext<>(TestScoped.class));
		abd.addContext(new ScopeContext<>(TestUnitScoped.class));
	}
}
