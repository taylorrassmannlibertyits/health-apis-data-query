package gov.va.api.health.argonaut.service.controller;

import gov.va.api.health.argonaut.api.bundle.BundleLink;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

/**
 * This provides paging links for bundles. It will create links for first, self, and last always. It
 * will conditionally create previous and next links.
 */
public interface PageLinks {

  /** Create a list of parameters that will contain 3 to 5 values. */
  List<BundleLink> create(LinkConfig config);

  @Getter
  @Builder
  class LinkConfig {
    /** The resource path without the base URL or port. E.g. /api/Patient/1234 */
    private final String path;

    private final int recordsPerPage;
    private final int page;
    private final int totalRecords;
    private final MultiValueMap<String, String> queryParams;
  }
}