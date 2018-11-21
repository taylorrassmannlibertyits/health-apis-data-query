package gov.va.api.health.argonaut.api.resources;

import static gov.va.api.health.argonaut.api.RoundTrip.assertRoundTrip;

import gov.va.api.health.argonaut.api.bundle.AbstractBundle.BundleType;
import gov.va.api.health.argonaut.api.bundle.AbstractEntry;
import gov.va.api.health.argonaut.api.bundle.BundleLink;
import gov.va.api.health.argonaut.api.bundle.BundleLink.LinkRelation;
import gov.va.api.health.argonaut.api.resources.Immunization.Bundle;
import gov.va.api.health.argonaut.api.resources.Immunization.Entry;
import gov.va.api.health.argonaut.api.samples.SampleImmunizations;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ImmunizationTest {

  private final SampleImmunizations data = SampleImmunizations.get();

  @Test
  public void bundlerCanBuildImmunizationBundles() {
    Entry entry =
        Entry.builder()
            .extension(Collections.singletonList(data.extension()))
            .fullUrl("http://immunization.com")
            .id("123")
            .link(
                Collections.singletonList(
                    BundleLink.builder()
                        .relation(LinkRelation.self)
                        .url(("http://immunization.com/1"))
                        .build()))
            .resource(data.immunization())
            .search(data.search())
            .request(data.request())
            .response(data.response())
            .build();

    Bundle bundle =
        Bundle.builder()
            .entry(Collections.singletonList(entry))
            .link(
                Collections.singletonList(
                    BundleLink.builder()
                        .relation(LinkRelation.self)
                        .url(("http://immunization.com/2"))
                        .build()))
            .type(BundleType.searchset)
            .build();

    assertRoundTrip(bundle);

    AbstractEntry.Search.builder().build().id();
  }

  @Test
  public void immunization() {
    assertRoundTrip(data.immunization());
  }
}
