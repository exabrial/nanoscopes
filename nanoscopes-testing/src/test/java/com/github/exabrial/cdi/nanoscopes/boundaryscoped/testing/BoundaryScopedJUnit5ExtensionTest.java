package com.github.exabrial.cdi.nanoscopes.boundaryscoped.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.apache.openwebbeans.junit5.Cdi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.exabrial.cdi.nanoscopes.boundaryscoped.BoundaryScopedFeature;
import com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.interceptor.Boundary;
import com.github.exabrial.cdi.nanoscopes.boundaryscoped.api.scope.BoundaryScoped;

@Cdi(disableDiscovery = true, recursivePackages = { BoundaryScopedFeature.class })
@ExtendWith(BoundaryScopedJUnit5Extension.class)
public class BoundaryScopedJUnit5ExtensionTest {

	@Inject
	private BoundaryScopedCounter boundaryScopedCounter;

	@Test
	@Boundary
	void sameInstanceForTheDurationOfABoundary() {
		assertEquals(1, boundaryScopedCounter.increment());
		assertEquals(2, boundaryScopedCounter.increment());
		assertEquals(3, boundaryScopedCounter.increment());
	}

	@Test
	@Boundary
	void freshInstanceInANewBoundary() {
		assertEquals(1, boundaryScopedCounter.increment());
		assertEquals(2, boundaryScopedCounter.increment());
	}
}

@BoundaryScoped
class BoundaryScopedCounter {
	private int count;

	int increment() {
		return ++count;
	}
}
