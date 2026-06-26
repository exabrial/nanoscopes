package com.github.exabrial.cdi.nanoscopes.testscoped.internal;

import java.io.Serializable;

import jakarta.enterprise.inject.Produces;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitMetadata;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitName;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitScopeKey;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitScoped;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@TestUnitScoped
@ToString
@EqualsAndHashCode
class TestUnitMetadataImpl implements TestUnitMetadata, Serializable {
	private static final long serialVersionUID = 1L;

	@Setter(AccessLevel.PACKAGE)
	private String testUnitName;
	@Setter(AccessLevel.PACKAGE)
	private String testUnitScopeKey;

	@Override
	@Produces
	@TestUnitName
	public String getTestUnitName() {
		return testUnitName;
	}

	@Override
	@Produces
	@TestUnitScopeKey
	public String getTestUnitScopeKey() {
		return testUnitScopeKey;
	}
}
