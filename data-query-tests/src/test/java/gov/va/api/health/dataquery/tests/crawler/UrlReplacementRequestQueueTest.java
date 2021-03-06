package gov.va.api.health.dataquery.tests.crawler;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.categories.Local;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Local.class)
public class UrlReplacementRequestQueueTest {
  UrlReplacementRequestQueue rq =
      UrlReplacementRequestQueue.builder()
          .replaceUrl("https://dev-api.va.gov/services/argonaut/v0/")
          .withUrl("https://staging-argonaut.lighthouse.va.gov/api/")
          .requestQueue(new ConcurrentResourceBalancingRequestQueue())
          .build();

  @Test(expected = IllegalArgumentException.class)
  public void emptyBaseUrlThrowsIllegalStateException() {
    UrlReplacementRequestQueue emptyBaseUrl =
        UrlReplacementRequestQueue.builder()
            .replaceUrl("")
            .withUrl("https://staging-argonaut.lighthouse.va.gov/api/")
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    emptyBaseUrl.add("Empty forceUrl");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyForceUrlThrowsIllegalStateException() {
    UrlReplacementRequestQueue emptyBaseUrl =
        UrlReplacementRequestQueue.builder()
            .replaceUrl("https://dev-api.va.gov/services/argonaut/v0/")
            .withUrl("")
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    emptyBaseUrl.add("Empty forceUrl");
  }

  @Test(expected = IllegalStateException.class)
  public void exceptionIsThrownWhenAttemptingToGetNextFromEmptyQueue() {
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    rq.next();
    rq.next();
  }

  @Test(expected = IllegalStateException.class)
  public void exceptionIsThrownWhenAttemptingToGetNextQueueThatWasNeverUsed() {
    rq.next();
  }

  @Test
  public void forceUrlReplacesBaseUrl() {
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    String expected =
        "https://staging-argonaut.lighthouse.va.gov/api/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99";
    assertThat(rq.next()).isEqualTo(expected);
  }

  @Test
  public void hasNextReturnsFalseForEmptyQueue() {
    assertThat(rq.hasNext()).isFalse();
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    rq.next();
    assertThat(rq.hasNext()).isFalse();
  }

  @Test
  public void itemsAreRemovedFromQueueInOrderOfAddition() {
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/Condition/ea59bc29-d507-571b-a4c6-9ac0d2146c45");
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/Immunization/00f4000a-b1c9-5190-993a-644569d2722b");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/Condition/ea59bc29-d507-571b-a4c6-9ac0d2146c45");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/Immunization/00f4000a-b1c9-5190-993a-644569d2722b");
    assertThat(rq.hasNext()).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullBaseUrlThrowsIllegalStateException() {
    UrlReplacementRequestQueue emptyBaseUrl =
        UrlReplacementRequestQueue.builder()
            .replaceUrl(null)
            .withUrl("https://staging-argonaut.lighthouse.va.gov/api/")
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    emptyBaseUrl.add("Empty baseUrl");
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullForceUrlThrowsIllegalStateException() {
    UrlReplacementRequestQueue emptyBaseUrl =
        UrlReplacementRequestQueue.builder()
            .replaceUrl("https://dev-api.va.gov/services/argonaut/v0/")
            .withUrl(null)
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    emptyBaseUrl.add("Empty forceUrl");
  }

  @Test
  public void replaceUrlAppendsSlash() {
    UrlReplacementRequestQueue appendToReplaceUrlRq =
        UrlReplacementRequestQueue.builder()
            .replaceUrl("https://dev-api.va.gov/services/argonaut/v0")
            .withUrl("https://staging-argonaut.lighthouse.va.gov/api/")
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    appendToReplaceUrlRq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    String expected =
        "https://staging-argonaut.lighthouse.va.gov/api/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99";
    assertThat(appendToReplaceUrlRq.next()).isEqualTo(expected);
  }

  @Test
  public void theSameItemCannotBeAddedToTheQueueTwice() {
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/Condition/ea59bc29-d507-571b-a4c6-9ac0d2146c45");
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/Immunization/00f4000a-b1c9-5190-993a-644569d2722b");
    assertThat(rq.hasNext()).isTrue();
    // ignored
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    // ignored
    rq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/Condition/ea59bc29-d507-571b-a4c6-9ac0d2146c45");
    assertThat(rq.hasNext()).isTrue();
    assertThat(rq.next())
        .isEqualTo(
            "https://staging-argonaut.lighthouse.va.gov/api/Immunization/00f4000a-b1c9-5190-993a-644569d2722b");
    assertThat(rq.hasNext()).isFalse();
  }

  @Test
  public void withUrlAppendsSlash() {
    UrlReplacementRequestQueue appendToWithUrlRq =
        UrlReplacementRequestQueue.builder()
            .replaceUrl("https://dev-api.va.gov/services/argonaut/v0/")
            .withUrl("https://staging-argonaut.lighthouse.va.gov/api")
            .requestQueue(new ConcurrentResourceBalancingRequestQueue())
            .build();
    appendToWithUrlRq.add(
        "https://dev-api.va.gov/services/argonaut/v0/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99");
    String expected =
        "https://staging-argonaut.lighthouse.va.gov/api/AllergyIntolerance/3be00408-b0ff-598d-8ba1-1e0bbfb02b99";
    assertThat(appendToWithUrlRq.next()).isEqualTo(expected);
  }
}
