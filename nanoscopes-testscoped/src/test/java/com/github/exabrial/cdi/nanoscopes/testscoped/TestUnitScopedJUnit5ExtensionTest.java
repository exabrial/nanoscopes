package com.github.exabrial.cdi.nanoscopes.testscoped;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.apache.openwebbeans.junit5.Cdi;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.exabrial.cdi.nanoscopes.testscoped.api.TestUnitScoped;

@Cdi(disableDiscovery = true, classes = { TestUnitScopedCounter.class }, recursivePackages = { TestScopedFeature.class })
@ExtendWith(TestScopedJUnit5Extension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUnitScopedJUnit5ExtensionTest {

	@Inject
	private TestUnitScopedCounter testUnitScopedCounter;

	@Test
	@Order(1)
	void firstTestStartsTheSharedInstance() {
		assertEquals(1, testUnitScopedCounter.increment());
	}

	@Test
	@Order(2)
	void secondTestSeesTheSameInstance() {
		assertEquals(2, testUnitScopedCounter.increment());
	}
}

@TestUnitScoped
class TestUnitScopedCounter {
	private int count;

	int increment() {
		return ++count;
	}
}
