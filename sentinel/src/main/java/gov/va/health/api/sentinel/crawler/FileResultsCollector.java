package gov.va.health.api.sentinel.crawler;

import static java.nio.charset.StandardCharsets.UTF_8;

import gov.va.health.api.sentinel.crawler.Result.Outcome;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * A implementation of the results collector that prints each result to a file and creates a
 * separate summary file to give information about all of the results.
 */
@Slf4j
@RequiredArgsConstructor
public class FileResultsCollector implements ResultCollector {

  private final File directory;
  private final Set<String> summary = new ConcurrentSkipListSet<>();

  @Override
  @SneakyThrows
  public void add(Result result) {
    String filename = createFilename(result.query());
    String basicInfo = filename + "," + result.outcome() + "," + result.query();
    summary.add(basicInfo);
    log.info("{} {}", result.query(), result.outcome());
    if (result.outcome() != Outcome.OK) {
      log.error("{}", result.body());
      log.error("{}", result.additionalInfo());
    }
    printBody(filename, result);
    printMetadata(filename, result);
  }

  /**
   * Creates small filename from a query, uses slashes to split for reads and question marks for
   * searches.
   */
  private String createFilename(String query) {
    String[] splitQuery = query.split("/");
    String resourceName = splitQuery[splitQuery.length - 2];
    String params = splitQuery[splitQuery.length - 1];
    if (params.contains("?")) {
      String[] searchParts = params.split("\\?");
      resourceName = searchParts[0];
      params = searchParts[1].replaceAll("patient", "P");
    }
    resourceName = resourceName.replaceAll("([a-z]{2})([a-z]+)", "$1");
    String filename = resourceName + params;
    return filename.replaceAll("[^A-Za-z0-9]", "");
  }

  @Override
  @SneakyThrows
  public void done() {
    String csv = summary.stream().sorted().collect(Collectors.joining("\n"));
    log.info("Summary:\n{}", csv);
    log.info("Made {} requests", summary.size());
    Files.write(
        new File(directory, "summary.csv").toPath(),
        summary,
        UTF_8,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.CREATE);
  }

  @Override
  public void init() {
    if (directory.exists()) {
      directory.delete();
    }
    directory.mkdirs();
  }

  private void printBody(String filename, Result result) throws IOException {
    String json =
        StringUtils.isBlank(result.body())
            ? "{ \"message\":\"No body for request.\"}"
            : result.body();
    Files.write(
        new File(directory, filename + ".json").toPath(),
        json.getBytes(UTF_8),
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.CREATE);
  }

  @SneakyThrows
  private void printMetadata(String filename, Result result) {
    try (PrintWriter text =
        new PrintWriter(new FileOutputStream(new File(directory, filename + ".txt")))) {
      text.print("QUERY: ");
      text.println(result.query());
      text.print("TIMESTAMP: ");
      text.println(result.timestamp());
      text.print("HTTP_STATUS: ");
      text.println(result.httpStatus());
      text.print("OUTCOME: ");
      text.println(result.outcome());
      if (StringUtils.isNotBlank(result.additionalInfo())) {
        text.println("----");
        text.println(result.additionalInfo());
      }
    }
  }
}
