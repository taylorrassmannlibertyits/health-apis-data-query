package gov.va.api.health.dataquery.tests.crawler;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.argonaut.api.resources.Medication;
import gov.va.api.health.argonaut.api.resources.MedicationStatement;
import gov.va.api.health.argonaut.api.resources.Patient;
import gov.va.api.health.dataquery.tests.crawler.UrlToResourceConverter.DoNotUnderstandUrl;
import gov.va.api.health.sentinel.categories.Local;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Local.class)
public class UrlToResourceConverterTest {
  UrlToResourceConverter converter = new UrlToResourceConverter();

  @Test
  public void conversions() {
    assertThat(converter.apply("https://argonaut.com/api/Patient/123")).isEqualTo(Patient.class);
    assertThat(converter.apply("https://argonaut.com/api/Patient?_id=123"))
        .isEqualTo(Patient.Bundle.class);
    assertThat(converter.apply("http://something.com/what/ever/Medication/123"))
        .isEqualTo(Medication.class);
    assertThat(converter.apply("http://something.com/what/ever/Medication/123?oh=boy"))
        .isEqualTo(Medication.class);
    assertThat(converter.apply("http://something.com/what/ever/MedicationStatement?patient=123"))
        .isEqualTo(MedicationStatement.Bundle.class);

    assertThat(
            converter.apply(
                "http://something.com/what/ever/MedicationStatement?what=ever&patient=123&who=cares"))
        .isEqualTo(MedicationStatement.Bundle.class);
  }

  @Test(expected = DoNotUnderstandUrl.class)
  public void noSuchClass() {
    converter.apply("http://something.com/api/WhatIsThisClass/123");
  }

  @Test(expected = DoNotUnderstandUrl.class)
  public void notParseable() {
    converter.apply("http://what-is-this-nonsense.com");
  }

  @Test(expected = DoNotUnderstandUrl.class)
  public void relativeUrlsNotSupported() {
    converter.apply("/api/Patient/123");
  }
}
