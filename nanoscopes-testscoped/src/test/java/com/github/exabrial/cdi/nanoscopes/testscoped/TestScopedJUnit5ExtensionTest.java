package com.github.exabrial.cdi.nanoscopes.testscoped;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.apache.openwebbeans.junit5.Cdi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestMetadata;
import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestScoped;

@Cdi(disableDiscovery = true, classes = { TestScopedCounter.class }, recursivePackages = { TestScopedFeature.class })
@ExtendWith(TestScopedJUnit5Extension.class)
public class TestScopedJUnit5ExtensionTest {

	@Inject
	private TestScopedCounter testScopedCounter;

	@Inject
	private TestMetadata testMetadata;

	@Test
	void freshInstanceForEachTest() {
		assertEquals(1, testScopedCounter.increment());
		assertEquals(2, testScopedCounter.increment());
	}

	@Test
	void anotherTestStartsFresh() {
		assertEquals(1, testScopedCounter.increment());
	}

	@Test
	void metadataReflectsTheRunningTest() {
		assertEquals("metadataReflectsTheRunningTest()", testMetadata.getTestDisplayName());
		assertEquals("TestScopedJUnit5ExtensionTest", testMetadata.getTestUnitName());
		assertTrue(testMetadata.getTestScopeKey().startsWith("TestScoped:junit5:"));
		assertTrue(testMetadata.getTestUnitScopeKey().startsWith("TestUnitScoped:junit5:"));
	}
}

@TestScoped
class TestScopedCounter {
	private int count;

	int increment() {
		return ++count;
	}
}
