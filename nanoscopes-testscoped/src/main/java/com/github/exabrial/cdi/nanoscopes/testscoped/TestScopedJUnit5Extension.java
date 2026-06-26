package com.github.exabrial.cdi.nanoscopes.testscoped;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.exabrial.cdi.nanoscopes.testscoped.internal.TestScopedLifecycleController;

public class TestScopedJUnit5Extension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		TestScopedLifecycleController.beforeAll(context);
	}

	@Override
	public void beforeEach(final ExtensionContext context) {
		TestScopedLifecycleController.beforeEach(context);
	}

	@Override
	public void afterEach(final ExtensionContext context) {
		TestScopedLifecycleController.afterEach(context);
	}

	@Override
	public void afterAll(final ExtensionContext context) throws Exception {
		TestScopedLifecycleController.afterAll(context);
	}
}
