package gov.va.api.health.argonaut.service.controller.encounter;

import static gov.va.api.health.argonaut.service.controller.Transformers.firstPayloadItem;
import static gov.va.api.health.argonaut.service.controller.Transformers.hasPayload;

import gov.va.api.health.argonaut.api.resources.Encounter;
import gov.va.api.health.argonaut.api.resources.OperationOutcome;
import gov.va.api.health.argonaut.service.controller.Bundler;
import gov.va.api.health.argonaut.service.controller.Bundler.BundleContext;
import gov.va.api.health.argonaut.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.argonaut.service.controller.Parameters;
import gov.va.api.health.argonaut.service.controller.Validator;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.argonaut.service.mranderson.client.Query;
import gov.va.dvp.cdw.xsd.model.CdwEncounter101Root;
import java.util.Collections;
import java.util.function.Function;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Encounter, see http://www.hl7.org/fhir/DSTU2/encounter.html for
 * implementation details.
 */
@SuppressWarnings("WeakerAccess")
@Validated
@RestController
@RequestMapping(
  value = {"/api/Encounter"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class EncounterController {

  private Transformer transformer;
  private MrAndersonClient mrAndersonClient;
  private Bundler bundler;

  private Encounter.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    CdwEncounter101Root root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Encounter")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(root.getRecordCount().intValue())
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            root.getEncounters() == null
                ? Collections.emptyList()
                : root.getEncounters().getEncounter(),
            transformer,
            Encounter.Entry::new,
            Encounter.Bundle::new));
  }

  /** Read by id. */
  @GetMapping(value = {"/{publicId}"})
  public Encounter read(@PathVariable("publicId") String publicId) {

    return transformer.apply(
        firstPayloadItem(
            hasPayload(search(Parameters.forIdentity(publicId)).getEncounters().getEncounter())));
  }

  private CdwEncounter101Root search(MultiValueMap<String, String> params) {
    Query<CdwEncounter101Root> query =
        Query.forType(CdwEncounter101Root.class)
            .profile(Query.Profile.ARGONAUT)
            .resource("Encounter")
            .version("1.01")
            .parameters(params)
            .build();
    return mrAndersonClient.search(query);
  }

  /** Search by _id. */
  @GetMapping(params = {"_id"})
  public Encounter.Bundle searchById(
      @RequestParam("_id") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(1) @Max(20) int count) {
    return bundle(
        Parameters.builder().add("identifier", id).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Search by Identifier. */
  @GetMapping(params = {"identifier"})
  public Encounter.Bundle searchByIdentifier(
      @RequestParam("identifier") String id,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(1) @Max(20) int count) {
    return bundle(
        Parameters.builder().add("identifier", id).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Encounter.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer
      extends Function<CdwEncounter101Root.CdwEncounters.CdwEncounter, Encounter> {}
}
