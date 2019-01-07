package gov.va.api.health.argonaut.api.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gov.va.api.health.argonaut.api.Fhir;
import gov.va.api.health.argonaut.api.bundle.AbstractBundle;
import gov.va.api.health.argonaut.api.bundle.AbstractEntry;
import gov.va.api.health.argonaut.api.bundle.BundleLink;
import gov.va.api.health.argonaut.api.datatypes.Annotation;
import gov.va.api.health.argonaut.api.datatypes.CodeableConcept;
import gov.va.api.health.argonaut.api.datatypes.Identifier;
import gov.va.api.health.argonaut.api.datatypes.Signature;
import gov.va.api.health.argonaut.api.datatypes.SimpleResource;
import gov.va.api.health.argonaut.api.elements.BackboneElement;
import gov.va.api.health.argonaut.api.elements.Extension;
import gov.va.api.health.argonaut.api.elements.Meta;
import gov.va.api.health.argonaut.api.elements.Narrative;
import gov.va.api.health.argonaut.api.elements.Reference;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonAutoDetect(
  fieldVisibility = JsonAutoDetect.Visibility.ANY,
  isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
@Schema(
  description =
      "http://www.fhir.org/guides/argonaut/r2/StructureDefinition-argo-allergyintolerance.html",
  example =
      "{ \n"
          + "    resourceType: \"AllergyIntolerance\", \n"
          + "    id: \"6f9a021b-07d5-53c8-8cce-b49a694d4ad9\", \n"
          + "    onset: \"1995-04-30T01:15:52Z\", \n"
          + "    patient: { \n"
          + "       reference: \"https://dev-api.va.gov/services/argonaut/v0/Patient/2000163\", \n"
          + "        display: \"Mr. Aurelio227 Cruickshank494\" \n"
          + "    }, \n"
          + "    substance: { \n"
          + "        text: \"Allergy to peanuts\" \n"
          + "   }, \n"
          + "    status: \"active\", \n"
          + "    type: \"allergy\", \n"
          + "    category: \"food\", \n"
          + "    note: { \n"
          + "        time: \"1995-04-30T01:15:52Z\", \n"
          + "        text: \"Allergy to peanuts\" \n"
          + "    }, \n"
          + "    reaction: [ \n"
          + "        { \n"
          + "            manifestation: [ \n"
          + "                { \n"
          + "                    coding: [ \n"
          + "                        { \n"
          + "                            display: \"Inflammation of Skin\", \n"
          + "                            system: \"urn:oid:2.16.840.1.113883.6.233\", \n"
          + "                            code: \"2000001\" \n"
          + "                        } \n"
          + "                    ], \n"
          + "                    text: \"Inflammation of Skin\" \n"
          + "                } \n"
          + "            ], \n"
          + "            certainty: \"likely\" \n"
          + "        } \n"
          + "    ] \n"
          + "} "
)
public class AllergyIntolerance implements Resource {
  @NotBlank String resourceType;

  @Pattern(regexp = Fhir.ID)
  String id;

  @Valid Meta meta;

  @Pattern(regexp = Fhir.URI)
  String implicitRules;

  @Pattern(regexp = Fhir.CODE)
  String language;

  @Valid Narrative text;
  @Valid List<SimpleResource> contained;
  @Valid List<Extension> extension;
  @Valid List<Extension> modifierExtension;
  @Valid List<Identifier> identifier;

  @Pattern(regexp = Fhir.DATETIME)
  String onset;

  @Pattern(regexp = Fhir.DATETIME)
  String recordedDate;

  @Valid Reference recorder;
  @NotNull @Valid Reference patient;
  @Valid Reference reporter;
  @NotNull @Valid CodeableConcept substance;
  @NotNull Status status;
  Criticality criticality;
  Type type;
  Category category;

  @Pattern(regexp = Fhir.DATETIME)
  String lastOccurence;

  @Valid Annotation note;
  @Valid List<Reaction> reaction;

  @SuppressWarnings("unused")
  public enum Status {
    active,
    unconfirmed,
    confirmed,
    inactive,
    resolved,
    refuted,
    @JsonProperty("entered-in-error")
    entered_in_error
  }

  @SuppressWarnings("unused")
  public enum Criticality {
    CRITL,
    CRITH,
    CRITU
  }

  @SuppressWarnings("unused")
  public enum Type {
    allergy,
    intolerance
  }

  @SuppressWarnings("unused")
  public enum Category {
    food,
    medication,
    environment,
    other
  }

  @SuppressWarnings("unused")
  public enum Certainty {
    unlikely,
    likely,
    confirmed
  }

  @SuppressWarnings("unused")
  public enum Severity {
    mild,
    moderate,
    severe
  }

  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(builder = AllergyIntolerance.Bundle.BundleBuilder.class)
  @Schema(
    name = "AllergyIntoleranceBundle",
    example =
        "{ \n"
            + "    resourceType: \"Bundle\", \n"
            + "    type: \"searchset\", \n"
            + "    total: 1, \n"
            + "    link: [ \n"
            + "        { \n"
            + "            relation: \"self\", \n"
            + "            url: \"https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance?patient=1017283148V813263&page=1&_count=15\" \n"
            + "        }, \n"
            + "        { \n"
            + "            relation: \"first\", \n"
            + "            url: \"https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance?patient=1017283148V813263&page=1&_count=15\" \n"
            + "        }, \n"
            + "        { \n"
            + "            relation: last\", \n"
            + "            url: \"https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance?patient=1017283148V813263&page=1&_count=15\" \n"
            + "        } \n"
            + "    ], \n"
            + "    entry: [ \n"
            + "        { \n"
            + "            fullUrl: \"https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/6f9a021b-07d5-53c8-8cce-b49a694d4ad9\", \n"
            + "            resource: { \n"
            + "                fullUrl: \"https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/e2019e0c-fa38-596d-b966-9b86926959a7\", \n"
            + "                resource: { \n"
            + "                    resourceType: \"AllergyIntolerance\", \n"
            + "                    id: \"e2019e0c-fa38-596d-b966-9b86926959a7\", \n"
            + "                    onset: \"1995-04-30T01:15:52Z\", \n"
            + "                    patient: { \n"
            + "                        reference: \"https://dev-api.va.gov/services/argonaut/v0/Patient/2000163\", \n"
            + "                        display: \"Mr. Aurelio227 Cruickshank494\" \n"
            + "                    }, \n"
            + "                    substance: { \n"
            + "                        text: \"Allergy to bee venom\" \n"
            + "                    }, \n"
            + "                    status: \"active\", \n"
            + "                    type: \"allergy\", \n"
            + "                    note: { \n"
            + "                        time: \"1995-04-30T01:15:52Z\", \n"
            + "                        text: \"Allergy to bee venom\" \n"
            + "                    }, \n"
            + "                    reaction: [ \n"
            + "                        { \n"
            + "                            manifestation: [ \n"
            + "                                { \n"
            + "                                    coding: [ \n"
            + "                                        { \n"
            + "                                            display: \"Sneezing and Coughing\", \n"
            + "                                            system: \"urn:oid:2.16.840.1.233\", \n"
            + "                                            code: \"2000004\" \n"
            + "                                        } \n"
            + "                                    ], \n"
            + "                                    text: \"Sneezing and Coughing\" \n"
            + "                                } \n"
            + "                            ] \n"
            + "                        } \n"
            + "                    ] \n"
            + "                }, \n"
            + "                search: { \n"
            + "                    mode: \"match\" \n"
            + "                } \n"
            + "        } \n"
            + "    ] \n"
            + "} "
  )
  public static class Bundle extends AbstractBundle<AllergyIntolerance.Entry> {

    @Builder
    public Bundle(
        @NotBlank String resourceType,
        @Pattern(regexp = Fhir.ID) String id,
        @Valid Meta meta,
        @Pattern(regexp = Fhir.URI) String implicitRules,
        @Pattern(regexp = Fhir.CODE) String language,
        @NotNull BundleType type,
        @Min(0) Integer total,
        @Valid List<BundleLink> link,
        @Valid List<AllergyIntolerance.Entry> entry,
        @Valid Signature signature) {
      super(resourceType, id, meta, implicitRules, language, type, total, link, entry, signature);
    }
  }

  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(builder = AllergyIntolerance.Entry.EntryBuilder.class)
  @Schema(name = "AllergyIntoleranceEntry")
  public static class Entry extends AbstractEntry<AllergyIntolerance> {

    @Builder
    public Entry(
        @Pattern(regexp = Fhir.ID) String id,
        @Valid List<Extension> extension,
        @Valid List<Extension> modifierExtension,
        @Valid List<BundleLink> link,
        @Pattern(regexp = Fhir.URI) String fullUrl,
        @Valid AllergyIntolerance resource,
        @Valid Search search,
        @Valid Request request,
        @Valid Response response) {
      super(id, extension, modifierExtension, link, fullUrl, resource, search, request, response);
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(name = "AllergyIntoleranceReaction")
  public static class Reaction implements BackboneElement {
    @Pattern(regexp = Fhir.ID)
    String id;

    @Valid List<Extension> modifierExtension;
    @Valid List<Extension> extension;
    @Valid CodeableConcept substance;
    Certainty certainty;
    @NotEmpty @Valid List<CodeableConcept> manifestation;
    String description;

    @Pattern(regexp = Fhir.DATETIME)
    String onset;

    Severity severity;
    @Valid CodeableConcept exposureRoute;
    @Valid Annotation note;
  }
}
