package com.github.exabrial.cdi.nanoscopes.testscoped.api;

public interface TestMetadata {
	String getTestDisplayName();

	String getTestScopeKey();

	String getTestUnitName();

	String getTestUnitScopeKey();
}
