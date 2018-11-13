package gov.va.api.health.argonaut.service.controller.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.health.argonaut.api.Patient;
import gov.va.api.health.argonaut.service.controller.Parameters;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.argonaut.service.mranderson.client.Query;
import gov.va.dvp.cdw.xsd.pojos.Patient103Root;
import gov.va.dvp.cdw.xsd.pojos.Patient103Root.Patients;
import java.util.List;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class PatientControllerTest {

  @Mock MrAndersonClient client;

  @Mock PatientController.Transformer tx;

  @Mock PatientController.Bundler bundler;

  PatientController controller;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new PatientController(tx, client, bundler);
  }

  @Test
  public void read() {
    Patient103Root root = new Patient103Root();
    root.setPatients(new Patients());
    Patient103Root.Patients.Patient xmlPatient = new Patient103Root.Patients.Patient();
    root.getPatients().getPatient().add(xmlPatient);
    Patient patient = Patient.builder().build();
    when(client.search(Mockito.any())).thenReturn(root);
    when(tx.apply(xmlPatient)).thenReturn(patient);
    Patient actual = controller.read("hello");
    assertThat(actual).isSameAs(patient);
    ArgumentCaptor<Query<Patient103Root>> captor = ArgumentCaptor.forClass(Query.class);
    verify(client).search(captor.capture());
    Entry<? extends String, ? extends List<String>> e;
    assertThat(captor.getValue().parameters()).isEqualTo(Parameters.forIdentity("hello"));
  }
}
