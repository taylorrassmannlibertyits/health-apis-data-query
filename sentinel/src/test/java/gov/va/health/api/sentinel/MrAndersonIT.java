package gov.va.health.api.sentinel;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class MrAndersonIT {

  private IdRegistrar registrar = IdRegistrar.of(Sentinel.get().system());

  private TestIds ids() {
    return registrar.registeredIds();
  }

  @Test
  public void invalidCountSpecifiedReturns400() {
    mrAnderson()
        .get("/api/v1/resources/argonaut/Patient/1.03?identifier={id}&_count=-1", ids().patient())
        .expect(400);
  }

  @Test
  public void invalidPageSpecifiedReturns400() {
    mrAnderson()
        .get("/api/v1/resources/argonaut/Patient/1.03?identifier={id}&page=-1", ids().patient())
        .expect(400);
  }

  @Test
  public void invalidQueryParamsReturns400() {
    mrAnderson().get("/api/v1/resources/argonaut/Patient/1.03?stuff=missing").expect(400);
  }

  private TestClient mrAnderson() {
    return Sentinel.get().clients().mrAnderson();
  }

  @Test
  public void noResultsReturns200WithEmptyResults() {
    String id = registrar.register("DIAGNOSTIC_REPORT", "5555555555555-mra-it");
    String records =
        mrAnderson()
            .get("/api/v1/resources/argonaut/DiagnosticReport/1.02?identifier={id}", id)
            .expect(200)
            .response()
            .path("root.recordCount");
    assertThat(records).isEqualTo("0");
  }

  @Test
  public void pageAndCountCanBeOmittedAndDefaultToOneAnd15() {
    mrAnderson()
        .get("/api/v1/resources/argonaut/DiagnosticReport/1.02?patient={id}", ids().patient())
        .expect(200);
  }

  @Test
  public void patientCanBeReadAfterIdHasBeenRegistered() {
    String cdwId =
        mrAnderson()
            .get("/api/v1/resources/argonaut/Patient/1.03?identifier={id}", ids().patient())
            .expect(200)
            .response()
            .path("root.patients.patient.cdwId");
    assertThat(cdwId).isEqualTo(Sentinel.get().system().cdwIds().patient());
  }

  @Test
  public void recordsCanBeSearchedByPatientThenReadById() {
    String id =
        mrAnderson()
            .get(
                "/api/v1/resources/argonaut/DiagnosticReport/1.02?patient={id}&page=1&_count=15",
                ids().patient())
            .expect(200)
            .response()
            .path("root.diagnosticReports.diagnosticReport[0].cdwId");
    String id2 =
        mrAnderson()
            .get("/api/v1/resources/argonaut/DiagnosticReport/1.02?identifier={id}", id)
            .expect(200)
            .response()
            .path("root.diagnosticReports.diagnosticReport[0].cdwId");
    assertThat(id2).isEqualTo(id);
  }

  @Test
  public void unknownResourceReturns404() {
    mrAnderson()
        .get("/api/v1/resources/argonaut/Patient/9.99?identifier=" + ids().patient())
        .expect(404);
  }

  @Test
  public void unregisteredIdReturns404() {
    mrAnderson()
        .get("/api/v1/resources/argonaut/Patient/1.03?identifier=" + ids().unknown())
        .expect(404);
  }
}