package gov.va.api.health.argonaut.service.controller.patient;

import static gov.va.api.health.argonaut.service.controller.Transformers.firstPayloadItem;
import static gov.va.api.health.argonaut.service.controller.Transformers.hasPayload;

import gov.va.api.health.argonaut.api.Patient;
import gov.va.api.health.argonaut.api.Patient.Bundle;
import gov.va.api.health.argonaut.service.controller.Bundler;
import gov.va.api.health.argonaut.service.controller.Bundler.BundleContext;
import gov.va.api.health.argonaut.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.argonaut.service.controller.Parameters;
import gov.va.api.health.argonaut.service.mranderson.client.MrAndersonClient;
import gov.va.api.health.argonaut.service.mranderson.client.Query;
import gov.va.dvp.cdw.xsd.model.CdwPatient103Root;
import gov.va.dvp.cdw.xsd.model.CdwPatient103Root.CdwPatients.CdwPatient;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for the Argonaut Patient Profile, see
 * https://www.fhir.org/guides/argonaut/r2/StructureDefinition-argo-patient.html for implementation
 * details.
 */
@RestController
@RequestMapping(
  value = {"/api/Patient"},
  produces = {"application/json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Slf4j
public class PatientController {

  private Transformer patientTransformer;
  private MrAndersonClient mrAndersonClient;
  private Bundler bundler;

  private Bundle bundle(
      MultiValueMap<String, String> parameters,
      int page,
      int count,
      HttpServletRequest servletRequest) {
    CdwPatient103Root root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path(servletRequest.getRequestURI())
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(root.getRecordCount())
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            root.getPatients().getPatient(),
            patientTransformer,
            Patient.Entry::new,
            Patient.Bundle::new));
  }

  /** Read by id. */
  @GetMapping(value = {"/{publicId}"})
  public Patient read(@PathVariable("publicId") String publicId) {
    return patientTransformer.apply(
        firstPayloadItem(
            hasPayload(search(Parameters.forIdentity(publicId)).getPatients()).getPatient()));
  }

  private CdwPatient103Root search(MultiValueMap<String, String> params) {
    Query<CdwPatient103Root> query =
        Query.forType(CdwPatient103Root.class)
            .profile(Query.Profile.ARGONAUT)
            .resource("Patient")
            .version("1.03")
            .parameters(params)
            .build();
    return mrAndersonClient.search(query);
  }

  /** Search by Family+Gender. */
  @GetMapping(params = {"family", "gender"})
  public Patient.Bundle searchByFamilyAndGender(
      @RequestParam("family") String family,
      @RequestParam("gender") String gender,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "_count", defaultValue = "15") int count,
      HttpServletRequest servletRequest) {

    return bundle(
        Parameters.builder()
            .add("family", family)
            .add("gender", gender)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count,
        servletRequest);
  }
  //
  //  /** Search by Given+Gender. */
  //  @GetMapping(params = {"given", "gender"})
  //  public Patient.Bundle searchByGivenAndGender(
  //      @RequestParam("given") String given, @RequestParam("gender") String gender) {
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.put("given", Arrays.asList(given));
  //    params.put("gender", Arrays.asList(gender));
  //    return bundler.apply(search(params));
  //  }
  //
  //  /** Search by _id. */
  //  @GetMapping(params = {"_id"})
  //  public Patient.Bundle searchById(@RequestParam("_id") String id) {
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.put("_id", Arrays.asList(id));
  //    return bundler.apply(search(params));
  //  }
  //
  //  /** Search by Identifier. */
  //  @GetMapping(params = {"identifier"})
  //  public Patient.Bundle searchByIdentifier(@RequestParam("identifier") String identifier) {
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.put("identifier", Arrays.asList(identifier));
  //    return bundler.apply(search(params));
  //  }
  //
  //  /** Search by Name+Birthdate. */
  //  @GetMapping(params = {"name", "birthdate"})
  //  public Patient.Bundle searchByNameAndBirthdate(
  //      @RequestParam("name") String name, @RequestParam("birthdate") String[] birthdate) {
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.put("name", Arrays.asList(name));
  //    params.put("birthdate", Arrays.asList(birthdate));
  //    return bundler.apply(search(params));
  //  }
  //
  //  /** Search by Name+Gender. */
  //  @GetMapping(params = {"name", "gender"})
  //  public Patient.Bundle searchByNameAndGender(
  //      @RequestParam("name") String name, @RequestParam("gender") String gender) {
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.put("name", Arrays.asList(name));
  //    params.put("gender", Arrays.asList(gender));
  //    return bundler.apply(search(params));
  //  }

  public interface Transformer extends Function<CdwPatient, Patient> {}
}
