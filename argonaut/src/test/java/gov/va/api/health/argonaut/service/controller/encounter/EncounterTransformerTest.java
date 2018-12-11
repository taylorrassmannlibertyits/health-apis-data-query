package gov.va.api.health.argonaut.service.controller.encounter;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.argonaut.api.datatypes.CodeableConcept;
import gov.va.api.health.argonaut.api.datatypes.Coding;
import gov.va.api.health.argonaut.api.datatypes.Period;
import gov.va.api.health.argonaut.api.elements.Reference;
import gov.va.api.health.argonaut.api.resources.Encounter;
import gov.va.api.health.argonaut.api.resources.Encounter.EncounterClass;
import gov.va.api.health.argonaut.api.resources.Encounter.EncounterLocation;
import gov.va.api.health.argonaut.api.resources.Encounter.Participant;
import gov.va.api.health.argonaut.api.resources.Encounter.Status;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter.CdwIndications;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter.CdwLocations;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter.CdwLocations.CdwLocation;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter.CdwParticipants;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root.CdwEncounters.CdwEncounter.CdwParticipants.CdwParticipant;
import gov.va.dvp.cdw.xsd.model.CdwEncounterClass;
import gov.va.dvp.cdw.xsd.model.CdwEncounterParticipantType;
import gov.va.dvp.cdw.xsd.model.CdwEncounterParticipantType.CdwCoding;
import gov.va.dvp.cdw.xsd.model.CdwEncounterParticipantTypeCode;
import gov.va.dvp.cdw.xsd.model.CdwEncounterParticipantTypeDisplay;
import gov.va.dvp.cdw.xsd.model.CdwEncounterPeriod;
import gov.va.dvp.cdw.xsd.model.CdwEncounterStatus;
import gov.va.dvp.cdw.xsd.model.CdwReference;
import java.math.BigInteger;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Test;

public class EncounterTransformerTest {

  private EncounterTransformer tx = new EncounterTransformer();
  private CdwSampleData cdw = new CdwSampleData();
  private Expected expected = new Expected();

  @Test
  public void encounter() {
    assertThat(tx.apply(cdw.encounter())).isEqualTo(expected.encounter());
  }

  @Test
  public void coding() {
    assertThat(tx.encounterParticipantTypeCoding(null)).isNull();
    assertThat(tx.encounterParticipantTypeCoding(new CdwCoding())).isNull();
    assertThat(tx.encounterParticipantTypeCoding(cdw.coding())).isEqualTo(expected.coding());
  }

  @Test
  public void reference() {
    assertThat(tx.reference(null)).isNull();
    assertThat(tx.reference(cdw.reference("x", "y"))).isEqualTo(expected.reference("x", "y"));
  }

  @Test
  public void period() {
    assertThat(tx.period(null)).isNull();
    assertThat(tx.period(cdw.period())).isEqualTo(expected.period());
  }

  @Test
  public void episodeOfCare() {
    assertThat(tx.episodeOfCare(null)).isNull();
    assertThat(tx.episodeOfCare(cdw.episodeOfCare())).isEqualTo(expected.episodeOfCare());
  }

  @Test
  public void location() {
    assertThat(tx.location(null)).isNull();
    assertThat(tx.location(new CdwLocations())).isNull();
    assertThat(tx.location(cdw.locations())).isEqualTo(expected.location());
  }

  @Test
  public void indications() {
    assertThat(tx.indications(null)).isNull();
    assertThat(tx.indications(new CdwIndications())).isNull();
    assertThat(tx.indications(cdw.indications())).isEqualTo(expected.indications());
  }

  @Test
  public void type() {
    assertThat(tx.encounterParticipantType(null)).isNull();
    assertThat(tx.encounterParticipantType(singletonList(cdw.participantType())))
        .isEqualTo(expected.type());
  }

  @Test
  public void participant() {
    assertThat(tx.participant(null)).isNull();
    assertThat(tx.participant(new CdwParticipants())).isNull();
    assertThat(tx.participant(cdw.participants())).isEqualTo(expected.participant());
  }

  @Test
  public void encounterClass() {
    assertThat(tx.encounterClass(CdwEncounterClass.AMBULATORY))
        .isEqualTo(EncounterClass.ambulatory);
    assertThat(tx.encounterClass(CdwEncounterClass.DAYTIME)).isEqualTo(EncounterClass.daytime);
    assertThat(tx.encounterClass(CdwEncounterClass.EMERGENCY)).isEqualTo(EncounterClass.emergency);
    assertThat(tx.encounterClass(CdwEncounterClass.FIELD)).isEqualTo(EncounterClass.field);
    assertThat(tx.encounterClass(CdwEncounterClass.HOME)).isEqualTo(EncounterClass.home);
    assertThat(tx.encounterClass(CdwEncounterClass.INPATIENT)).isEqualTo(EncounterClass.inpatient);
    assertThat(tx.encounterClass(CdwEncounterClass.OTHER)).isEqualTo(EncounterClass.other);
    assertThat(tx.encounterClass(CdwEncounterClass.OUTPATIENT))
        .isEqualTo(EncounterClass.outpatient);
    assertThat(tx.encounterClass(CdwEncounterClass.VIRTUAL)).isEqualTo(EncounterClass.virtual);
  }

  @Test
  public void encounterStatus() {
    assertThat(tx.encounterStatus(CdwEncounterStatus.ARRIVED)).isEqualTo(Status.arrived);
    assertThat(tx.encounterStatus(CdwEncounterStatus.CANCELLED)).isEqualTo(Status.cancelled);
    assertThat(tx.encounterStatus(CdwEncounterStatus.FINISHED)).isEqualTo(Status.finished);
    assertThat(tx.encounterStatus(CdwEncounterStatus.IN_PROGRESS)).isEqualTo(Status.in_progress);
    assertThat(tx.encounterStatus(CdwEncounterStatus.ONLEAVE)).isEqualTo(Status.onleave);
    assertThat(tx.encounterStatus(CdwEncounterStatus.PLANNED)).isEqualTo(Status.planned);
  }

  @NoArgsConstructor(staticName = "get")
  private static class CdwSampleData {

    private CdwEncounter encounter() {
      CdwEncounter cdw = new CdwEncounter();
      cdw.setAppointment(appointment());
      cdw.setCdwId("1200753214085");
      cdw.setClazz(CdwEncounterClass.EMERGENCY);
      cdw.setStatus(CdwEncounterStatus.FINISHED);
      cdw.setServiceProvider(serviceProvider());
      cdw.setEpisodeOfCare(episodeOfCare());
      cdw.setIndications(indications());
      cdw.setLocations(locations());
      cdw.setParticipants(participants());
      cdw.setPatient(patient());
      cdw.setPeriod(period());
      cdw.setRowNumber(BigInteger.ONE);
      return cdw;
    }

    @SneakyThrows
    private XMLGregorianCalendar dateTime(String timestamp) {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(timestamp);
    }

    private CdwEncounterPeriod period() {
      CdwEncounterPeriod cdw = new CdwEncounterPeriod();
      cdw.setStart(dateTime("2015-04-15T14:25:00Z"));
      cdw.setEnd(dateTime("2015-04-15T17:16:00Z"));
      return cdw;
    }

    private CdwParticipants participants() {
      CdwParticipants cdw = new CdwParticipants();
      cdw.getParticipant().add(participant());
      return cdw;
    }

    private CdwParticipant participant() {
      CdwParticipant cdw = new CdwParticipant();
      cdw.setIndividual(reference("Practitioner/1885906", "JONES,RESIDENT B"));
      cdw.getType().add(participantType());
      return cdw;
    }

    private CdwIndications indications() {
      CdwIndications cdw = new CdwIndications();
      cdw.getIndication().add(indication());
      return cdw;
    }

    private CdwLocations locations() {
      CdwLocations cdw = new CdwLocations();
      cdw.getLocation().add(location());
      return cdw;
    }

    private CdwLocation location() {
      CdwLocation cdw = new CdwLocation();
      cdw.setLocationReference(reference("Location/1200007523:L", "GNV ED"));
      return cdw;
    }

    private CdwReference reference(String ref, String display) {
      CdwReference cdw = new CdwReference();
      cdw.setReference(ref);
      cdw.setDisplay(display);
      return cdw;
    }

    private CdwReference serviceProvider() {
      return reference("Organizaton/173039:I", "N. FLORIDA/S. GEORGIA HCS");
    }

    private CdwReference indication() {
      return reference(
          "Condition/1200760238107:D", "Chronic asthmatic bronchitis (SNOMED CT 195949008)");
    }

    private CdwReference episodeOfCare() {
      return reference("EpisodeOfCare/1234", "Episode 3");
    }

    private CdwReference appointment() {
      return reference("Appointment/1200438317388", "Appointment");
    }

    private CdwReference patient() {
      return reference("Patient/185601V825290", "VETERAN,JOHN Q");
    }

    private CdwEncounterParticipantType participantType() {
      CdwEncounterParticipantType cdw = new CdwEncounterParticipantType();
      cdw.setCoding(coding());
      cdw.setText("Translates language");
      return cdw;
    }

    private CdwCoding coding() {
      CdwCoding cdw = new CdwCoding();
      cdw.setDisplay(CdwEncounterParticipantTypeDisplay.ADMITTER);
      cdw.setCode(CdwEncounterParticipantTypeCode.ADM);
      cdw.setSystem("http://hl7.org/fhir/participant-type");
      return cdw;
    }
  }

  @NoArgsConstructor(staticName = "get")
  private static class Expected {
    private Encounter encounter() {
      return Encounter.builder()
          .resourceType("Encounter")
          .id("1200753214085")
          .period(period())
          .encounterClass(EncounterClass.emergency)
          .status(Status.finished)
          .location(location())
          .participant(participant())
          .indication(indications())
          .episodeOfCare(episodeOfCare())
          .appointment(appointment())
          .patient(patient())
          .serviceProvider(serviceProvider())
          .build();
    }

    private List<EncounterLocation> location() {
      return singletonList(
          EncounterLocation.builder()
              .location(reference("Location/1200007523:L", "GNV ED"))
              .build());
    }

    private Period period() {
      return Period.builder().start("2015-04-15T14:25:00Z").end("2015-04-15T17:16:00Z").build();
    }

    private Reference reference(String ref, String display) {
      return Reference.builder().reference(ref).display(display).build();
    }

    private List<Participant> participant() {
      return singletonList(
          Participant.builder()
              .individual(reference("Practitioner/1885906", "JONES,RESIDENT B"))
              .type(type())
              .build());
    }

    private List<CodeableConcept> type() {
      return singletonList(
          CodeableConcept.builder().coding(coding()).text("Translates language").build());
    }

    private List<Coding> coding() {
      return singletonList(
          Coding.builder()
              .system("http://hl7.org/fhir/participant-type")
              .display("admitter")
              .code("ADM")
              .build());
    }

    private List<Reference> indications() {
      return singletonList(
          reference(
              "Condition/1200760238107:D", "Chronic asthmatic bronchitis (SNOMED CT 195949008)"));
    }

    private List<Reference> episodeOfCare() {
      return singletonList(reference("EpisodeOfCare/1234", "Episode 3"));
    }

    private Reference appointment() {
      return reference("Appointment/1200438317388", "Appointment");
    }

    private Reference patient() {
      return reference("Patient/185601V825290", "VETERAN,JOHN Q");
    }

    private Reference serviceProvider() {
      return reference("Organizaton/173039:I", "N. FLORIDA/S. GEORGIA HCS");
    }
  }
}