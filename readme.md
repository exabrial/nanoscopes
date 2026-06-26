# nanoscopes

A collection of small, useful CDI scopes.

Built for Jakarta EE 10 on JDK 25, tested against OpenWebBeans.

## Overview

### Main Scopes for use in Applications

### `nanoscopes`

```xml
<dependency>
  <groupId>com.github.exabrial.cdi.nanoscopes</groupId>
  <artifactId>nanoscopes</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

- **`@BoundaryScoped`** one instance for the duration of a `@Boundary`-annotated call, torn down when it returns. A stand-in for request scope where there is no request: SE apps, timers, batch jobs, entry-point methods. Reentrant, so nested `@Boundary` calls share the same instance.
- **`@MessageScoped`** one instance per message handled, scoped to a `@MessageBoundary` listener method. Gives each inbound message (e.g. a JMS `onMessage`) isolated, automatically cleaned-up state. Nests inside a boundary.

### Testing

Everything in this project should be compatible with _any_ CDI implementation (Weld, etc), but I find  [OpenWebBeans](https://openwebbeans.apache.org/), the Apache CDI implementation, to be a very fluid experience. Run in SE mode so each test boots a real CDI container with no application server.

Add OpenWebBeans at test scope:

```xml
<dependency>
  <groupId>org.apache.openwebbeans</groupId>
  <artifactId>openwebbeans-impl</artifactId>
  <version>4.0.3</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.apache.openwebbeans</groupId>
  <artifactId>openwebbeans-se</artifactId>
  <version>4.0.3</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.apache.openwebbeans</groupId>
  <artifactId>openwebbeans-junit5</artifactId>
  <version>4.0.3</version>
  <scope>test</scope>
</dependency>
```

The `@Cdi` annotation (from `openwebbeans-junit5`) starts an OpenWebBeans SE container for the annotated test class and injects into the test instance. Its attributes control what lands in that container:

- `disableDiscovery = true` turns off automatic bean scanning, so the container holds only what you explicitly list. This keeps each test's bean graph small and predictable.
- `classes = { ... }` adds individual bean classes by hand.
- `recursivePackages = { SomeClass.class }` scans the package of each listed class (and its subpackages) across the classpath and registers every bean found. Anchoring on a scope's `Feature` class pulls in that scope's CDI extension, interceptor, and beans in one shot.


### `nanoscopes-testing`

```xml
<dependency>
  <groupId>com.github.exabrial.cdi.nanoscopes</groupId>
  <artifactId>nanoscopes-testing</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

Test support for the `nanoscopes` scopes. In production a `@BoundaryScoped` or `@MessageScoped` bean only has a live context while a `@Boundary` or `@MessageBoundary` method is on the stack; in a unit test there is no such call. `nanoscopes-testing` provides JUnit 5 extensions (`BoundaryScopedJUnit5Extension`, `MessageScopedJUnit5Extension`) that enter the matching context before each test and tear it down after, firing the usual `@Initialized` and `@Destroyed` events.

Register the extension on your test and mark the test method (or the whole class) with the matching boundary annotation. The context is entered only for tests that carry the annotation.

The bean under test is an ordinary `@BoundaryScoped` bean:

```java
@BoundaryScoped
public class AddressFormatter {

  public String format(final RawAddress rawAddress) {
    // build and return the formatted address
  }
}
```

```java
@Cdi(disableDiscovery = true,
			classes = { AddressFormatter.class },
			recursivePackages = { BoundaryScopedFeature.class })
@ExtendWith({ BoundaryScopedJUnit5Extension.class})
public class AddressFormatterTest {

  @Inject
  private AddressFormatter addressFormatter; // a @BoundaryScoped bean

  @Test
  @Boundary // Activates BoundaryScope for just this test
  void formatsInsideABoundaryScope() {
    // a BoundaryScoped context is live for this test; the bean and any
    // @BoundaryScoped collaborators resolve here and are destroyed afterward
    assertEquals("123 Main St", addressFormatter.format(rawAddress));
  }
}
```



### `nanoscopes-testscoped` (JUnit 5)

This adds two additional useful scopes for testing.

```xml
<dependency>
  <groupId>com.github.exabrial.cdi.nanoscopes</groupId>
  <artifactId>nanoscopes-testscoped</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>test</scope>
</dependency>
```

- **`@TestScoped`** a fresh instance for each `@Test` method, destroyed afterward. Use for per-test fixtures you want isolated between tests.
- **`@TestUnitScoped`** one instance shared across every test in a class, for the life of that class. Use for state set up once and read by all tests in the unit.

## License

Licensed under EUPL-1.2. You can safely use unmodified code in closed-source commercial projects without revealing your proprietary application code. If you modify nanoscopes and distribute it, or offer online access through a modified version, the law requires you to publish your nanoscopes changeset first; that does not include your own application code.

See [contributing.md](contributing.md) to pitch in.

## Verifying artifacts

All release artifacts are signed with Jonathan's GPG key:

```
Fingerprint: 871638A21A7F2C38066471420306A354336B4F0D
```

```bash
gpg --keyserver keyserver.ubuntu.com --recv-keys 871638A21A7F2C38066471420306A354336B4F0D

find ~/.m2/repository/com/github/exabrial/cdi/nanoscopes -name '*.asc' -exec gpg --verify {} \;
```
