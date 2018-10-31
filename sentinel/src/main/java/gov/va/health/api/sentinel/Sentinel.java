package gov.va.health.api.sentinel;

import java.util.Locale;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * The Sentinel provides the easy-to-use entry point to determining and accessing test
 * configurations that are specific for different environments.
 *
 * <p>It leverages the system property `sentinel` to determine which environment configuration
 * should be leveraged.
 */
@Value
@Slf4j
public class Sentinel {

  private SystemDefinition system;

  /**
   * Create a new Sentinel configured with the system definition based on the environment specified
   * by the `sentinel` system property.
   */
  public static Sentinel get() {
    String env = System.getProperty("sentinel", "LOCAL").toUpperCase(Locale.ENGLISH);
    switch (env) {
      case "LOCAL":
        return new Sentinel(SystemDefinitions.get().local());
      default:
        throw new IllegalArgumentException("Unknown sentinel environment: " + env);
    }
  }

  public TestClients clients() {
    return TestClients.of(system());
  }
}