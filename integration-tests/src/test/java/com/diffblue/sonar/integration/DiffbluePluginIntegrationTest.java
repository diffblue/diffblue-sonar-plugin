/*
 * Copyright 2026 Diffblue Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffblue.sonar.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for the Diffblue Coverage SonarQube Plugin.
 *
 * <p>These tests use TestContainers to:
 *
 * <ul>
 *   <li>Start a real SonarQube instance in Docker
 *   <li>Install the Diffblue Coverage plugin
 *   <li>Run analysis on test projects from test-examples/
 *   <li>Validate metrics via SonarQube REST API
 * </ul>
 *
 * <p>Prerequisites:
 *
 * <ul>
 *   <li>Docker must be running
 *   <li>Plugin JAR must be built (run 'mvn package' first)
 * </ul>
 */
@Testcontainers
class DiffbluePluginIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(DiffbluePluginIntegrationTest.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final HttpClient httpClient = HttpClient.newHttpClient();

  // Path to the built plugin JAR (relative to this module)
  private static final Path PLUGIN_JAR = resolvePluginJar();

  // SonarQube Docker image to test against. Override with -Dsonarqube.image=<tag> (or the
  // SONARQUBE_IMAGE env var) to validate compatibility with other versions. Defaults to
  // 9.9-community, the oldest version we support (first with ARM64 support).
  private static final String SONARQUBE_IMAGE = resolveSonarqubeImage();

  // SonarQube credentials - default credentials for test container
  // Note: These are only used in isolated test containers and never in production
  private static final String SONAR_USER = "admin";
  private static final String SONAR_PASSWORD = "admin";

  // Test file path used for file-level metric validation
  private static final String TEST_CALCULATOR_FILE = "src/main/java/com/example/Calculator.java";

  /**
   * SonarQube container configured with: - SonarQube image from {@link #SONARQUBE_IMAGE} (defaults
   * to 9.9 Community Edition, the oldest supported version) - Diffblue Coverage plugin mounted into
   * extensions/plugins/ - Health check waiting for API to be ready
   */
  @Container
  static GenericContainer<?> sonarqube =
      new GenericContainer<>(SONARQUBE_IMAGE)
          .withExposedPorts(9000)
          .withFileSystemBind(
              PLUGIN_JAR.toString(), "/opt/sonarqube/extensions/plugins/diffblue-coverage.jar")
          .withEnv("SONAR_ES_BOOTSTRAP_CHECKS_DISABLE", "true") // Disable Elasticsearch checks
          .waitingFor(
              Wait.forHttp("/api/system/status")
                  .forPort(9000)
                  .forStatusCode(200)
                  .forResponsePredicate(response -> response.contains("\"status\":\"UP\""))
                  .withStartupTimeout(Duration.ofMinutes(5)))
          .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix("SONARQUBE"));

  private static String baseUrl;

  @BeforeAll
  static void setUp() {
    // Verify plugin JAR exists
    assertTrue(
        Files.exists(PLUGIN_JAR),
        "Plugin JAR must exist at " + PLUGIN_JAR + ". Run 'mvn package' first.");

    // Get the dynamically assigned host and port
    baseUrl = "http://" + sonarqube.getHost() + ":" + sonarqube.getMappedPort(9000);
    logger.info("SonarQube image under test: {}", SONARQUBE_IMAGE);
    logger.info("SonarQube is running at: {}", baseUrl);

    // Verify the Diffblue Coverage plugin is installed
    logger.info("Verifying Diffblue Coverage plugin installation");
    try {
      String response = sendGet("/api/plugins/installed");
      JsonNode plugins = objectMapper.readTree(response);
      boolean pluginFound = false;
      for (JsonNode plugin : plugins.path("plugins")) {
        if ("diffbluecoverage".equals(plugin.path("key").asText())) {
          pluginFound = true;
          break;
        }
      }
      assertTrue(pluginFound, "Diffblue Coverage plugin should be installed");
      logger.info("Diffblue Coverage plugin verified successfully");
    } catch (Exception e) {
      throw new RuntimeException("Failed to verify plugin installation", e);
    }
  }

  /**
   * Resolves the SonarQube Docker image tag to test against.
   *
   * <p>Reads the {@code sonarqube.image} system property first, then the {@code SONARQUBE_IMAGE}
   * environment variable, falling back to the oldest supported version. This lets CI run the same
   * suite across a matrix of supported SonarQube versions.
   *
   * @return the Docker image coordinate (e.g. {@code sonarqube:26.7-community})
   */
  private static String resolveSonarqubeImage() {
    String image = System.getProperty("sonarqube.image");
    if (image == null || image.isBlank()) {
      image = System.getenv("SONARQUBE_IMAGE");
    }
    if (image == null || image.isBlank()) {
      image = "sonarqube:9.9-community";
    }
    return image;
  }

  /**
   * Dynamically resolves the plugin JAR path by searching for any diffblue-sonar-plugin-*.jar file.
   *
   * @return Path to the plugin JAR file
   * @throws IllegalStateException if the JAR cannot be found
   */
  private static Path resolvePluginJar() {
    Path targetDir = Paths.get("../sonar-plugin/target").toAbsolutePath().normalize();
    if (!Files.isDirectory(targetDir)) {
      throw new IllegalStateException("Plugin target directory not found: " + targetDir);
    }

    try (DirectoryStream<Path> stream =
        Files.newDirectoryStream(targetDir, "diffblue-sonar-plugin-*.jar")) {
      for (Path jar : stream) {
        return jar.toAbsolutePath().normalize();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to locate plugin JAR in " + targetDir, e);
    }

    throw new IllegalStateException(
        "Plugin JAR not found in " + targetDir + ". Run 'mvn package' first.");
  }

  /**
   * Creates a basic authentication header value.
   *
   * @return Base64 encoded authentication header
   */
  private static String getAuthHeader() {
    String auth = SONAR_USER + ":" + SONAR_PASSWORD;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Sends a GET request to SonarQube API.
   *
   * @param path API path (relative to base URL)
   * @return Response body as String
   * @throws IOException if request fails
   * @throws InterruptedException if request is interrupted
   */
  private static String sendGet(String path) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", getAuthHeader())
            .GET()
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(200, response.statusCode(), "GET " + path + " should return 200");
    return response.body();
  }

  /**
   * Sends a POST request to SonarQube API with form parameters.
   *
   * @param path API path (relative to base URL)
   * @param formParams Form parameters as key-value pairs
   * @param expectedStatus Expected HTTP status code
   * @return Response body as String
   * @throws IOException if request fails
   * @throws InterruptedException if request is interrupted
   */
  private static String sendPost(String path, Map<String, String> formParams, int expectedStatus)
      throws IOException, InterruptedException {
    String formBody =
        formParams.entrySet().stream()
            .map(
                entry ->
                    URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "="
                        + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", getAuthHeader())
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(formBody))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(
        expectedStatus, response.statusCode(), "POST " + path + " should return " + expectedStatus);
    return response.body();
  }

  /**
   * Builds a URL for querying component measures from SonarQube API.
   *
   * @param component Component key (project key or project:file path)
   * @param metricKeys Metric keys to query
   * @return URL path with query parameters
   */
  private static String buildMeasuresUrl(String component, String... metricKeys) {
    String encodedComponent = URLEncoder.encode(component, StandardCharsets.UTF_8);
    String encodedMetrics = String.join(",", metricKeys);
    return "/api/measures/component?component="
        + encodedComponent
        + "&metricKeys="
        + encodedMetrics;
  }

  /**
   * Builds a URL for querying the component tree from SonarQube API.
   *
   * @param projectKey Project key
   * @return URL path with query parameters
   */
  private static String buildComponentTreeUrl(String projectKey) {
    String encodedProjectKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
    return "/api/components/tree?component=" + encodedProjectKey;
  }

  @Test
  @DisplayName("Basic Coverage - Mixed Diffblue and Manual Tests")
  void testBasicCoverage() throws IOException, InterruptedException {
    String projectKey = "test-basic-coverage";
    logger.info("Running test: Basic Coverage (projectKey={})", projectKey);

    // Create the project first via Web API
    logger.info("Creating project via Web API");
    sendPost(
        "/api/projects/create", Map.of("project", projectKey, "name", "Test Basic Coverage"), 200);

    // Enable the Diffblue plugin for this project via Web API
    // MeasureComputers read from project settings (not analysis parameters)
    logger.info("Enabling Diffblue plugin for project via Web API");
    sendPost(
        "/api/settings/set",
        Map.of("component", projectKey, "key", "sonar.diffblue.toggle", "value", "true"),
        204);

    SonarAnalysisRunner.analyze(
        baseUrl, "../test-examples", projectKey, SONAR_USER, SONAR_PASSWORD);

    String apiResponse =
        sendGet(
            buildMeasuresUrl(
                projectKey,
                "diffblue.lines.coverable",
                "diffblue.lines.covered.diffblue",
                "diffblue.lines.covered.diffblue_only",
                "diffblue.percent.covered.diffblue",
                "diffblue.percent.covered.diffblue_only"));

    logger.debug("=== API Response for {} (project level) ===", projectKey);
    logger.debug("API Response: {}", apiResponse);
    logger.debug("=== End API Response ===");

    // Also try querying for file-level measures
    String fileResponse =
        sendGet(
            buildMeasuresUrl(
                projectKey + ":" + TEST_CALCULATOR_FILE,
                "diffblue.lines.coverable",
                "diffblue.lines.covered.diffblue",
                "diffblue.lines.covered.diffblue_only"));

    logger.debug("=== File-level API Response ===");
    logger.debug("File Response: {}", fileResponse);
    logger.debug("=== End File-level Response ===");

    // List all components in the project
    String componentsResponse = sendGet(buildComponentTreeUrl(projectKey));

    logger.debug("=== All Components ===");
    logger.debug("Components Response: {}", componentsResponse);
    logger.debug("=== End Components ===");

    try {
      JsonNode response = objectMapper.readTree(apiResponse);

      // Assert expected metrics
      assertMetric(response, "diffblue.lines.coverable", "27");
      assertMetric(response, "diffblue.lines.covered.diffblue", "27");
      assertMetric(response, "diffblue.lines.covered.diffblue_only", "22");
      assertMetric(response, "diffblue.percent.covered.diffblue", "100.0");
      assertMetric(response, "diffblue.percent.covered.diffblue_only", "81.5");

      logger.info("Test passed: Basic Coverage");
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON response", e);
    }
  }

  /**
   * Helper method to extract and assert metric values.
   *
   * @param response JSON response from SonarQube API
   * @param metricKey The metric key to check
   * @param expectedValue The expected value
   */
  private void assertMetric(JsonNode response, String metricKey, String expectedValue) {
    String actualValue = getMetricValue(response, metricKey);
    assertEquals(
        expectedValue,
        actualValue,
        String.format("Metric %s should equal %s", metricKey, expectedValue));
  }

  /**
   * Helper method to extract metric value from JSON response.
   *
   * @param response JSON response from SonarQube API
   * @param metricKey The metric key to extract
   * @return The metric value as a String
   */
  private String getMetricValue(JsonNode response, String metricKey) {
    JsonNode measures = response.path("component").path("measures");
    if (measures.isArray()) {
      for (JsonNode measure : measures) {
        if (metricKey.equals(measure.path("metric").asText())) {
          return measure.path("value").asText();
        }
      }
    }
    return null;
  }
}
