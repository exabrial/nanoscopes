package com.github.exabrial.cdi.nanoscopes.messagescoped.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.apache.openwebbeans.junit5.Cdi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.exabrial.cdi.nanoscopes.messagescoped.MessageScopedFeature;
import com.github.exabrial.cdi.nanoscopes.messagescoped.api.interceptor.MessageBoundary;
import com.github.exabrial.cdi.nanoscopes.messagescoped.api.scope.MessageScoped;

@Cdi(disableDiscovery = true, recursivePackages = { MessageScopedFeature.class })
@ExtendWith(MessageScopedJUnit5Extension.class)
public class MessageScopedJUnit5ExtensionTest {

	@Inject
	private MessageScopedCounter messageScopedCounter;

	@Test
	@MessageBoundary
	void sameInstanceForTheDurationOfAMessage() {
		assertEquals(1, messageScopedCounter.increment());
		assertEquals(2, messageScopedCounter.increment());
		assertEquals(3, messageScopedCounter.increment());
	}

	@Test
	@MessageBoundary
	void freshInstanceForANewMessage() {
		assertEquals(1, messageScopedCounter.increment());
		assertEquals(2, messageScopedCounter.increment());
	}
}

@MessageScoped
class MessageScopedCounter {
	private int count;

	int increment() {
		return ++count;
	}
}
