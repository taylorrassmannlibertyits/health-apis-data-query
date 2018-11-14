package gov.va.api.health.argonaut.api;

import static java.util.Collections.singletonList;

import gov.va.api.health.argonaut.api.Address.AddressType;
import gov.va.api.health.argonaut.api.Address.AddressUse;
import gov.va.api.health.argonaut.api.ContactPoint.ContactPointSystem;
import gov.va.api.health.argonaut.api.ContactPoint.ContactPointUse;
import gov.va.api.health.argonaut.api.HumanName.NameUse;
import gov.va.api.health.argonaut.api.Identifier.IdentifierUse;
import gov.va.api.health.argonaut.api.Issue.IssueSeverity;
import gov.va.api.health.argonaut.api.Patient.Gender;
import java.util.Arrays;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

/**
 * This class provides data structures that are populated with dummy values, suitable for testing
 * serialization.
 */
@SuppressWarnings("WeakerAccess")
@NoArgsConstructor(staticName = "get")
public class SamplePatients {

  @Delegate SampleDataTypes dataTypes = SampleDataTypes.get();

  public Address address() {
    return Address.builder()
        .id("1234")
        .extension(singletonList(extension()))
        .use(AddressUse.home)
        .type(AddressType.both)
        .text("Hello")
        .line(Arrays.asList("hello", "goodbye"))
        .city("Hello City")
        .district("Hello District")
        .state("Hello State")
        .postalCode("12345")
        .country("Hello Country")
        .period(period())
        .build();
  }

  public Communication communication() {
    return Communication.builder()
        .id("8888")
        .extension(singletonList(extension()))
        .modifierExtension(singletonList(extension()))
        .language(language())
        .preferred(false)
        .build();
  }

  Contact contact() {
    return Contact.builder()
        .id("0000")
        .extension(singletonList(extension()))
        .modifierExtension(singletonList(extension()))
        .relationship(singletonList(relationship()))
        .name(name())
        .telecom(singletonList(telecom()))
        .address(address())
        .gender(Contact.Gender.unknown)
        .organization(reference())
        .period(period())
        .build();
  }

  public CodeableConcept details() {
    return CodeableConcept.builder().coding(singletonList(coding())).text("HelloText").build();
  }

  public Identifier identifier() {
    return Identifier.builder()
        .id("5678")
        .use(IdentifierUse.official)
        .extension(singletonList(extension()))
        .build();
  }

  public Issue issue() {
    return Issue.builder()
        .severity(IssueSeverity.error)
        .code("HelloCode")
        .details(details())
        .diagnostics("HelloDiagnostics")
        .location(singletonList("HelloLocation"))
        .expression(singletonList("HelloExpression"))
        .build();
  }

  public CodeableConcept language() {
    return CodeableConcept.builder().coding(singletonList(coding())).text("HelloText").build();
  }

  public Link link() {
    return Link.builder()
        .id("7777")
        .extension(singletonList(extension()))
        .modifierExtension(singletonList(extension()))
        .other(reference())
        .type("HelloType")
        .build();
  }

  public CodeableConcept maritalStatus() {
    return CodeableConcept.builder().coding(singletonList(coding())).text("HelloText").build();
  }

  public HumanName name() {
    return HumanName.builder()
        .use(NameUse.anonymous)
        .text("HelloText")
        .family(singletonList("HelloFamily"))
        .given(singletonList("HelloGiven"))
        .prefix(singletonList("HelloPrefix"))
        .suffix(singletonList("HelloSuffix"))
        .period(period())
        .build();
  }

  public Patient patient() {
    return Patient.builder()
        .id("1234")
        .resourceType("Patient")
        .meta(meta())
        .implicitRules("http://HelloRules.com")
        .language("Hello Language")
        .text(narrative())
        .contained(singletonList(resource()))
        .extension(Arrays.asList(extension(), extension()))
        .modifierExtension(
            Arrays.asList(extension(), extensionWithQuantity(), extensionWithRatio()))
        .identifier(singletonList(identifier()))
        .active(true)
        .name(singletonList(name()))
        .telecom(singletonList(telecom()))
        .gender(Gender.unknown)
        .birthDate("2000-01-01")
        .deceasedBoolean(false)
        .address(singletonList(address()))
        .maritalStatus(maritalStatus())
        .multipleBirthBoolean(false)
        .photo(singletonList(photo()))
        .contact(singletonList(contact()))
        .communication(singletonList(communication()))
        .careProvider(singletonList(reference()))
        .managingOrganization(reference())
        .link(singletonList(link()))
        .build();
  }

  public Period period() {
    return Period.builder()
        .id("5678")
        .extension(singletonList(Extension.builder().url("http://wtf.com").valueInteger(1).build()))
        .start("2000-01-01T00:00:00-00:00")
        .end("2001-01-01T00:00:00-00:00")
        .build();
  }

  public Attachment photo() {
    return Attachment.builder()
        .contentType("HelloType")
        .language("HelloLanguage")
        .data("HelloData")
        .url("http://HelloUrl.com")
        .size(1)
        .hash("HelloHash")
        .title("HelloTitle")
        .creation("2000-01-01T00:00:00-00:00")
        .build();
  }

  public Range range() {
    return Range.builder().low(simpleQuantity()).high(simpleQuantity()).build();
  }

  public CodeableConcept relationship() {
    return CodeableConcept.builder().coding(singletonList(coding())).text("HelloText").build();
  }

  public ContactPoint telecom() {
    return ContactPoint.builder()
        .system(ContactPointSystem.other)
        .value("HelloValue")
        .use(ContactPointUse.home)
        .rank(1)
        .period(period())
        .build();
  }
}