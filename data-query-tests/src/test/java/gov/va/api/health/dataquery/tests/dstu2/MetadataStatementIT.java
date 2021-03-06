package gov.va.api.health.dataquery.tests.dstu2;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.dataquery.tests.TestClients;
import gov.va.api.health.dataquery.tests.categories.LabDataQueryClinician;
import gov.va.api.health.dataquery.tests.categories.LabDataQueryPatient;
import gov.va.api.health.dataquery.tests.categories.ProdDataQueryClinician;
import gov.va.api.health.dataquery.tests.categories.ProdDataQueryPatient;
import gov.va.api.health.dstu2.api.resources.Conformance;
import gov.va.api.health.r4.api.resources.CapabilityStatement;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.health.sentinel.categories.Local;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class MetadataStatementIT {
  private String apiPath() {
    return TestClients.internalDataQuery().service().apiPath();
  }

  @Test
  @Category({
    Local.class,
    LabDataQueryPatient.class,
    LabDataQueryClinician.class,
    ProdDataQueryPatient.class,
    ProdDataQueryClinician.class
  })
  public void dstu2ConformanceStatementIsValid() {
    ExpectedResponse response = TestClients.internalDataQuery().get(apiPath() + "dstu2/metadata");
    response.expect(200).expectValid(Conformance.class);
    String rawJson = response.response().asString();
    assertThat(rawJson)
        .withFailMessage("Tabs and newlines break our customer.")
        .doesNotContain("\n", "\t");
  }

  @Test
  @Category({
    Local.class,
    LabDataQueryPatient.class,
    LabDataQueryClinician.class,
    ProdDataQueryPatient.class,
    ProdDataQueryClinician.class
  })
  public void r4CapabilityStatementIsValid() {
    ExpectedResponse response = TestClients.internalDataQuery().get(apiPath() + "r4/metadata");
    response.expect(200).expectValid(CapabilityStatement.class);
    String rawJson = response.response().asString();
    assertThat(rawJson)
        .withFailMessage("Tabs and newlines break our customer.")
        .doesNotContain("\n", "\t");
  }
}
