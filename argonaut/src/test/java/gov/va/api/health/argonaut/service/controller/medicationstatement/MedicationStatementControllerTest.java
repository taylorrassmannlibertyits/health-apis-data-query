package gov.va.api.health.argonaut.service.controller.medicationstatement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.va.api.health.argonaut.api.bundle.AbstractBundle.BundleType;
import gov.va.api.health.argonaut.api.resources.MedicationStatement;
import gov.va.api.health.argonaut.api.resources.MedicationStatement.Bundle;
import gov.va.api.health.argonaut.service.controller.Bundler;
import gov.va.api.health.argonaut.service.controller.Bundler.BundleContext;
import gov.va.api.health.argonaut.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.argonaut.service.controller.Parameters;
import gov.va.api.health.argonaut.service.controller.Validator;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.argonaut.service.mranderson.client.Query;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.dvp.cdw.xsd.model.CdwMedicationStatement102Root;
import gov.va.dvp.cdw.xsd.model.CdwMedicationStatement102Root.CdwMedicationStatements;
import gov.va.dvp.cdw.xsd.model.CdwMedicationStatement102Root.CdwMedicationStatements.CdwMedicationStatement;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.util.MultiValueMap;

@SuppressWarnings("WeakerAccess")
public class MedicationStatementControllerTest {
  @Mock MrAndersonClient client;

  @Mock MedicationStatementController.Transformer tx;

  MedicationStatementController controller;
  @Mock HttpServletRequest servletRequest;
  @Mock Bundler bundler;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new MedicationStatementController(tx, client, bundler);
  }

  private void assertSearch(Supplier<Bundle> invocation, MultiValueMap<String, String> params) {
    CdwMedicationStatement102Root root = new CdwMedicationStatement102Root();
    root.setPageNumber(BigInteger.valueOf(1));
    root.setRecordsPerPage(BigInteger.valueOf(10));
    root.setRecordCount(BigInteger.valueOf(3));
    root.setMedicationStatements(new CdwMedicationStatements());
    CdwMedicationStatement cdwItem1 = new CdwMedicationStatement();
    CdwMedicationStatement cdwItem2 = new CdwMedicationStatement();
    CdwMedicationStatement cdwItem3 = new CdwMedicationStatement();
    root.getMedicationStatements()
        .getMedicationStatement()
        .addAll(Arrays.asList(cdwItem1, cdwItem2, cdwItem3));
    MedicationStatement patient1 = MedicationStatement.builder().build();
    MedicationStatement patient2 = MedicationStatement.builder().build();
    MedicationStatement patient3 = MedicationStatement.builder().build();
    when(tx.apply(cdwItem1)).thenReturn(patient1);
    when(tx.apply(cdwItem2)).thenReturn(patient2);
    when(tx.apply(cdwItem3)).thenReturn(patient3);
    when(client.search(Mockito.any())).thenReturn(root);
    when(servletRequest.getRequestURI()).thenReturn("/api/Patient");

    Bundle mockBundle = new Bundle();
    when(bundler.bundle(Mockito.any())).thenReturn(mockBundle);

    Bundle actual = invocation.get();

    assertThat(actual).isSameAs(mockBundle);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<
            BundleContext<
                CdwMedicationStatement, MedicationStatement, MedicationStatement.Entry, Bundle>>
        captor = ArgumentCaptor.forClass(BundleContext.class);

    verify(bundler).bundle(captor.capture());

    LinkConfig expectedLinkConfig =
        LinkConfig.builder()
            .page(1)
            .recordsPerPage(10)
            .totalRecords(3)
            .path("/api/Patient")
            .queryParams(params)
            .build();
    assertThat(captor.getValue().linkConfig()).isEqualTo(expectedLinkConfig);
    assertThat(captor.getValue().xmlItems())
        .isEqualTo(root.getMedicationStatements().getMedicationStatement());
    assertThat(captor.getValue().newBundle().get()).isInstanceOf(Bundle.class);
    assertThat(captor.getValue().newEntry().get()).isInstanceOf(MedicationStatement.Entry.class);
    assertThat(captor.getValue().transformer()).isSameAs(tx);
  }

  private Bundle bundleOf(MedicationStatement resource) {
    return Bundle.builder()
        .type(BundleType.searchset)
        .resourceType("Bundle")
        .entry(
            Collections.singletonList(
                MedicationStatement.Entry.builder()
                    .fullUrl("http://example.com")
                    .resource(resource)
                    .build()))
        .build();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void read() {
    CdwMedicationStatement102Root root = new CdwMedicationStatement102Root();
    root.setMedicationStatements(new CdwMedicationStatements());
    CdwMedicationStatement xmlMedicationStatement = new CdwMedicationStatement();
    root.getMedicationStatements().getMedicationStatement().add(xmlMedicationStatement);
    MedicationStatement item = MedicationStatement.builder().build();
    when(client.search(Mockito.any())).thenReturn(root);
    when(tx.apply(xmlMedicationStatement)).thenReturn(item);
    MedicationStatement actual = controller.read("hello");
    assertThat(actual).isSameAs(item);
    ArgumentCaptor<Query<CdwMedicationStatement102Root>> captor =
        ArgumentCaptor.forClass(Query.class);
    verify(client).search(captor.capture());
    assertThat(captor.getValue().parameters()).isEqualTo(Parameters.forIdentity("hello"));
  }

  @Test
  public void searchById() {
    assertSearch(
        () -> controller.searchById("me", 1, 10, servletRequest),
        Parameters.builder().add("_id", "me").add("page", 1).add("_count", 10).build());
  }

  @Test
  public void searchByIdentifier() {
    assertSearch(
        () -> controller.searchByIdentifier("me", 1, 10, servletRequest),
        Parameters.builder().add("identifier", "me").add("page", 1).add("_count", 10).build());
  }

  @Test
  public void searchByPatient() {
    assertSearch(
        () -> controller.searchByPatient("me", 1, 10, servletRequest),
        Parameters.builder().add("patient", "me").add("page", 1).add("_count", 10).build());
  }

  @Test
  public void searchByPatientAndInclude() {
    assertSearch(
        () ->
            controller.searchByPatientAndInclude(
                "me", "MedicationStatement:medication", 1, 10, servletRequest),
        Parameters.builder()
            .add("patient", "me")
            .add("_include", "MedicationStatement:medication")
            .add("page", 1)
            .add("_count", 10)
            .build());
  }

  @Test
  @SneakyThrows
  public void validateAcceptsValidBundle() {
    MedicationStatement resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/cdw/old-medicationStatement-1.02.json"),
                MedicationStatement.class);

    Bundle bundle = bundleOf(resource);
    assertThat(controller.validate(bundle)).isEqualTo(Validator.ok());
  }

  @Test(expected = ConstraintViolationException.class)
  @SneakyThrows
  public void validateThrowsExceptionForInvalidBundle() {
    MedicationStatement resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/cdw/old-medicationStatement-1.02.json"),
                MedicationStatement.class);
    resource.resourceType(null);

    Bundle bundle = bundleOf(resource);
    controller.validate(bundle);
  }
}
