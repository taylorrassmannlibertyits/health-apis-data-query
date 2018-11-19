package gov.va.api.health.autoconfig.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.Test;

public class JacksonConfigTest {

  @Test
  @SneakyThrows
  public void canCreateYamlMapper() {
    ObjectMapper mapper = JacksonConfig.createMapper(new YAMLFactory());
    CandyYaml actual = mapper.readValue("ya: neat\nml: 1", CandyYaml.class);
    assertThat(actual).isEqualTo(CandyYaml.builder().ya("neat").ml(1).build());
  }

  @Test
  @SneakyThrows
  public void defaultConstructorIsUsedWhenAvailable() {
    ObjectMapper mapper = JacksonConfig.createMapper();
    HasPrivateDefaultConstructor actual =
        mapper.readValue("{\"ok\":\"hey yah\"}", HasPrivateDefaultConstructor.class);
    HasPrivateDefaultConstructor expected =
        HasPrivateDefaultConstructor.unconventional().ok("hey yah").build();
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void hasEasyToUseMapperSupplier() {
    Supplier<ObjectMapper> supplier = JacksonConfig::createMapper;
    assertThat(supplier.get()).isNotNull();
  }

  @SuppressWarnings("WeakerAccess")
  @Value
  @Builder
  public static class CandyYaml {
    String ya;
    int ml;
  }

  @SuppressWarnings("WeakerAccess")
  @Data
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @Builder(builderClassName = "CannotAutoDetactMe", builderMethodName = "unconventional")
  public static class HasPrivateDefaultConstructor {
    String ok;
  }
}
