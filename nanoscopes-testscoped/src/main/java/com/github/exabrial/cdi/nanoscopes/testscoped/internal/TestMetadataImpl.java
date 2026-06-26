package com.github.exabrial.cdi.nanoscopes.testscoped.internal;

import java.io.Serializable;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestDisplayName;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestMetadata;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestScopeKey;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestScoped;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitMetadata;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@TestScoped
@ToString
@EqualsAndHashCode
class TestMetadataImpl implements TestMetadata, Serializable {
	private static final long serialVersionUID = 1L;

	@Setter(AccessLevel.PACKAGE)
	private String testDisplayName;
	@Setter(AccessLevel.PACKAGE)
	private String testScopeKey;

	@Inject
	private TestUnitMetadata testUnitMetadata;

	@Override
	@Produces
	@TestDisplayName
	public String getTestDisplayName() {
		return testDisplayName;
	}

	@Override
	@Produces
	@TestScopeKey
	public String getTestScopeKey() {
		return testScopeKey;
	}

	@Override
	public String getTestUnitName() {
		return testUnitMetadata.getTestUnitName();
	}

	@Override
	public String getTestUnitScopeKey() {
		return testUnitMetadata.getTestUnitScopeKey();
	}
}
