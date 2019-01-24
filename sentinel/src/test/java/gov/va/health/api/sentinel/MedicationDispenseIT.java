package gov.va.health.api.sentinel;

import static gov.va.health.api.sentinel.ResourceVerifier.test;

import gov.va.api.health.argonaut.api.resources.MedicationDispense;
import gov.va.api.health.argonaut.api.resources.OperationOutcome;
import gov.va.health.api.sentinel.categories.Prod;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class MedicationDispenseIT {

  ResourceVerifier verifier = ResourceVerifier.get();

  @Test
  public void advanced() {
    verifier.verifyAll(
        test(
            200,
            MedicationDispense.Bundle.class,
            "/api/MedicationDispense?_id={id}",
            verifier.ids().medicationDispense()),
        test(
            200,
            MedicationDispense.Bundle.class,
            "/api/MedicationDispense?identifier={id}",
            verifier.ids().medicationDispense()),
        test(
            404,
            OperationOutcome.class,
            "/api/MedicationDispense?_id={id}",
            verifier.ids().unknown()));
  }

  @Test
  @Category({Prod.class})
  public void basic() {
    verifier.verifyAll(
        test(
            200,
            MedicationDispense.class,
            "/api/MedicationDispense/{id}",
            verifier.ids().medicationDispense()),
        test(404, OperationOutcome.class, "/api/MedicationDispense/{id}", verifier.ids().unknown()),
        test(
            200,
            MedicationDispense.Bundle.class,
            "/api/MedicationDispense?patient={patient}",
            verifier.ids().patient()),
        test(
            200,
            MedicationDispense.Bundle.class,
            "/api/MedicationDispense?patient={patient}&status=stopped,completed",
            verifier.ids().patient()),
        test(
            200,
            MedicationDispense.Bundle.class,
            "/api/MedicationDispense?patient={patient}&type=FF,UD",
            verifier.ids().patient()));
  }
}
