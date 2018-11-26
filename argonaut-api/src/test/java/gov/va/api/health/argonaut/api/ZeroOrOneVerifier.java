package gov.va.api.health.argonaut.api;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.argonaut.api.datatypes.Address;
import gov.va.api.health.argonaut.api.datatypes.Age;
import gov.va.api.health.argonaut.api.datatypes.Attachment;
import gov.va.api.health.argonaut.api.datatypes.CodeableConcept;
import gov.va.api.health.argonaut.api.datatypes.Coding;
import gov.va.api.health.argonaut.api.datatypes.ContactPoint;
import gov.va.api.health.argonaut.api.datatypes.HumanName;
import gov.va.api.health.argonaut.api.datatypes.Identifier;
import gov.va.api.health.argonaut.api.datatypes.Period;
import gov.va.api.health.argonaut.api.datatypes.Quantity;
import gov.va.api.health.argonaut.api.datatypes.Range;
import gov.va.api.health.argonaut.api.datatypes.Ratio;
import gov.va.api.health.argonaut.api.datatypes.SampledData;
import gov.va.api.health.argonaut.api.elements.Reference;
import gov.va.api.health.argonaut.api.samples.SampleDataTypes;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * This class will verify fields with a given prefix are properly configured in the same ZeroOrOneOf
 * group. This class will fiend related fields with the same prefix and systematically test
 * different combinations to ensure they are validate as expected.
 */
@Slf4j
public class ZeroOrOneVerifier<T> {
  /** A valid sample. We will mutate this throughout the test. */
  private final T sample;
  /** Used to create sample values for fields based on the field type. */
  @Getter private final Map<Class<?>, Supplier<?>> knownTypes = createKnownTypes();
  /**
   * Used to create sample String values for fields based on the Pattern regex constraint on the
   * field.
   */
  @Getter private final Map<String, Supplier<?>> stringTypes = createKnownStringTypes();

  /** The determined list of related fields based on the prefix. */
  @Getter private final List<String> fields;

  /** The prefix of the related fields. */
  private String fieldPrefix;

  @Builder
  public ZeroOrOneVerifier(T sample, String fieldPrefix) {
    this.sample = sample;
    this.fieldPrefix = fieldPrefix;
    fields = findFieldsWithPrefix();
  }

  private static Map<String, Supplier<?>> createKnownStringTypes() {
    Map<String, Supplier<?>> suppliers = new HashMap<>();
    suppliers.put("", () -> "hello");
    suppliers.put(Fhir.ID, () -> "id");
    suppliers.put(Fhir.CODE, () -> "code");
    suppliers.put(Fhir.URI, () -> "http://example.com");
    suppliers.put(Fhir.BASE64, () -> "SSBqdXN0IGF0ZSBhIHBlYW51dAo=");
    suppliers.put(Fhir.DATE, () -> "2005-01-21");
    suppliers.put(Fhir.DATETIME, () -> "2005-01-21T07:57:00Z");
    suppliers.put(Fhir.TIME, () -> "07:57:00.000");
    suppliers.put(Fhir.INSTANT, () -> "2005-01-21T07:57:00.000Z");
    suppliers.put(Fhir.OID, () -> "urn:oid:0.1");
    suppliers.put(Fhir.XHTML, () -> "<div>html</div>");
    return suppliers;
  }

  private static Map<Class<?>, Supplier<?>> createKnownTypes() {
    SampleDataTypes dataTypes = SampleDataTypes.get();
    Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();
    suppliers.put(String.class, () -> "hello");
    suppliers.put(Integer.class, () -> 1);
    suppliers.put(Boolean.class, () -> true);
    suppliers.put(Double.class, () -> 1.0);
    suppliers.put(Coding.class, dataTypes::coding);
    suppliers.put(CodeableConcept.class, dataTypes::codeableConcept);
    suppliers.put(Identifier.class, dataTypes::identifier);
    suppliers.put(Quantity.class, dataTypes::quantity);
    suppliers.put(Attachment.class, dataTypes::attachment);
    suppliers.put(Range.class, dataTypes::range);
    suppliers.put(Period.class, dataTypes::period);
    suppliers.put(Ratio.class, dataTypes::ratio);
    suppliers.put(HumanName.class, dataTypes::humanName);
    suppliers.put(Address.class, dataTypes::address);
    suppliers.put(ContactPoint.class, dataTypes::contactPoint);
    suppliers.put(Reference.class, dataTypes::reference);
    suppliers.put(SampledData.class, dataTypes::sampledData);
    suppliers.put(Age.class, dataTypes::age);
    return suppliers;
  }

  @SneakyThrows
  private void assertProblems(int count) {
    Set<ConstraintViolation<T>> problems =
        Validation.buildDefaultValidatorFactory().getValidator().validate(sample);
    if (problems.size() == count) {
      return;
    }
    log.info(
        JacksonConfig.createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(sample));
    problems.forEach(p -> log.error("{}", p));
    assertThat(problems.size()).isEqualTo(count);
  }

  @SneakyThrows
  private Field field(String name) {
    Field field = sample.getClass().getDeclaredField(name);
    assertThat(field).withFailMessage("Cannot determine field type: " + name).isNotNull();
    return field;
  }

  private List<String> findFieldsWithPrefix() {
    return Arrays.stream(sample.getClass().getDeclaredFields())
        .map(Field::getName)
        .filter(name -> name.startsWith(fieldPrefix))
        .collect(toList());
  }

  @SneakyThrows
  private void setField(String field, Object value) {
    log.trace("Setting {} to {}", field, value);
    setter(field).invoke(sample, value);
  }

  private void setField(String name) {
    Field field = field(name);
    Supplier<?> supplier;
    if (String.class.equals(field.getType())) {
      Pattern pattern = field.getAnnotation(Pattern.class);
      String stringType = "";
      if (pattern != null) {
        stringType = pattern.regexp();
      }
      supplier = stringTypes().get(stringType);
    } else {
      supplier = knownTypes().get(field.getType());
    }
    assertThat(supplier)
        .withFailMessage("Unknown value type for field: " + name + " type: " + field.getType())
        .isNotNull();
    setField(name, supplier.get());
  }

  /** Finds the getter method of the property provided in order to access the value. */
  @SneakyThrows
  private Method setter(String name) {
    Method setter = null;
    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(sample.getClass(), name);
    if (pd != null) {
      setter = pd.getWriteMethod();
    }
    if (setter == null) {
      setter = BeanUtils.findMethod(sample.getClass(), name, field(name).getType());
    }
    if (setter == null) {
      throw new IllegalArgumentException(
          "Cannot find Java bean property or fluent setter: "
              + sample.getClass().getName()
              + "."
              + name);
    }
    return setter;
  }

  private void unsetFields() {
    fields.forEach(field -> setField(field, null));
  }

  public void verify() {
    log.info("Verifying {}", sample.getClass());
    /* Make sure the sample is valid before we mess it up. */
    assertProblems(0);

    /* Make sure we are valid if no fields are set. */
    unsetFields();
    assertProblems(0);

    /* Make sure setting any two fields is not ok. */
    log.info("{} fields in group {}: {}", sample.getClass().getSimpleName(), fieldPrefix, fields);
    assertThat(fields.size())
        .withFailMessage("Not enough fields in group: " + fieldPrefix)
        .isGreaterThan(1);
    String anchor = fields.get(0);
    for (int i = 1; i < fields.size(); i++) {
      unsetFields();
      setField(anchor);
      setField(fields.get(i));
      assertProblems(1);
    }
  }
}