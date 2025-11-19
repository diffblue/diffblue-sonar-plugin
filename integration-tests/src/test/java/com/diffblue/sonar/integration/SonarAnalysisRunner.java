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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to run SonarQube analysis on test projects.
 *
 * <p>Supports Maven projects. Detects the build tool by looking for pom.xml files.
 */
public class SonarAnalysisRunner {

  private static final Logger logger = LoggerFactory.getLogger(SonarAnalysisRunner.class);

  private static final int POLLING_INTERVAL_MS = 500; // Check every 500ms
  private static final int POLLING_TIMEOUT_MS = 60000; // Max wait time of 60 seconds
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Runs SonarQube analysis on the specified project with Diffblue plugin enabled.
   *
   * @param sonarUrl Base URL of the SonarQube instance
   * @param projectPath Path to the project directory (relative or absolute)
   * @param projectKey Unique key for the project in SonarQube
   * @param username SonarQube username
   * @param password SonarQube password
   */
  public static void analyze(
      String sonarUrl, String projectPath, String projectKey, String username, String password) {
    runAnalysis(sonarUrl, projectPath, projectKey, username, password, true);
  }

  /**
   * Runs SonarQube analysis on the specified project WITHOUT enabling Diffblue plugin. Used to test
   * that the plugin is disabled by default.
   *
   * @param sonarUrl Base URL of the SonarQube instance
   * @param projectPath Path to the project directory (relative or absolute)
   * @param projectKey Unique key for the project in SonarQube
   * @param username SonarQube username
   * @param password SonarQube password
   */
  public static void analyzeWithoutToggle(
      String sonarUrl, String projectPath, String projectKey, String username, String password) {
    runAnalysis(sonarUrl, projectPath, projectKey, username, password, false);
  }

  /**
   * Internal method to run SonarQube analysis.
   *
   * @param sonarUrl Base URL of the SonarQube instance
   * @param projectPath Path to the project directory
   * @param projectKey Unique key for the project in SonarQube
   * @param username SonarQube username
   * @param password SonarQube password
   * @param enableDiffblue Whether to enable the Diffblue plugin
   */
  private static void runAnalysis(
      String sonarUrl,
      String projectPath,
      String projectKey,
      String username,
      String password,
      boolean enableDiffblue) {
    try {
      Path projectDir = Paths.get(projectPath).toAbsolutePath().normalize();

      if (!Files.exists(projectDir)) {
        throw new IllegalArgumentException("Project directory does not exist: " + projectDir);
      }

      logger.info("Running SonarQube analysis on: {}", projectDir);
      logger.info("Project key: {}", projectKey);
      logger.info("Diffblue plugin enabled: {}", enableDiffblue);

      // Verify Maven project
      if (!Files.exists(projectDir.resolve("pom.xml"))) {
        throw new IllegalStateException("pom.xml not found in " + projectDir);
      }

      logger.info("Detected Maven project");
      ProcessBuilder pb =
          buildMavenCommand(projectDir, sonarUrl, projectKey, username, password, enableDiffblue);

      pb.directory(projectDir.toFile());
      pb.redirectErrorStream(true);

      // Run the process
      Process process = pb.start();

      // Capture and log output
      StringBuilder output = new StringBuilder();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
          // Log lines that might be relevant to debugging
          if (line.contains("ERROR")
              || line.contains("WARN")
              || line.contains("ANALYSIS")
              || line.contains("Diffblue")
              || line.contains("Sensor")
              || line.contains("INFO")) {
            logger.debug("SONAR: {}", line);
          }
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        logger.error("SonarQube analysis failed with exit code: {}", exitCode);
        logger.error("Full output:\n{}", output);
        throw new RuntimeException("SonarQube analysis failed with exit code: " + exitCode);
      }

      logger.info("SonarQube analysis completed successfully");

      // Wait for SonarQube to process the results by polling the Compute Engine status
      logger.info("Waiting for SonarQube to process results...");
      waitForSonarProcessing(sonarUrl, projectKey, username, password);

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Failed to run SonarQube analysis", e);
    }
  }

  /**
   * Waits for SonarQube to complete processing the analysis by polling the Compute Engine API.
   *
   * @param sonarUrl Base URL of the SonarQube instance
   * @param projectKey Unique key for the project
   * @param username SonarQube username
   * @param password SonarQube password
   * @throws InterruptedException if thread is interrupted while waiting
   */
  private static void waitForSonarProcessing(
      String sonarUrl, String projectKey, String username, String password)
      throws InterruptedException {
    long startTime = System.currentTimeMillis();
    String authHeader = createAuthHeader(username, password);
    HttpClient httpClient = HttpClient.newHttpClient();

    String encodedComponent = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
    String apiUrl = sonarUrl + "/api/ce/component?component=" + encodedComponent;
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Authorization", authHeader)
            .GET()
            .build();

    while (true) {
      long elapsedTime = System.currentTimeMillis() - startTime;

      if (elapsedTime >= POLLING_TIMEOUT_MS) {
        throw new RuntimeException(
            String.format(
                "Timeout waiting for SonarQube processing after %d ms", POLLING_TIMEOUT_MS));
      }

      try {
        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
          JsonNode root = objectMapper.readTree(response.body());
          JsonNode tasks = root.path("queue");

          // Check if there are any tasks in the queue
          if (tasks.isArray() && tasks.size() > 0) {
            for (JsonNode task : tasks) {
              String status = task.path("status").asText();
              if ("FAILED".equals(status) || "CANCELED".equals(status)) {
                throw new RuntimeException(
                    "SonarQube processing failed: " + task.path("status").asText());
              }
            }
            logger.debug("SonarQube still processing ({} tasks in queue)...", tasks.size());
          } else {
            // No tasks in queue, check if there's a current task or if processing is complete
            JsonNode current = root.path("current");
            if (current.isMissingNode() || current.isNull()) {
              // No current task and empty queue means processing is complete
              logger.info("SonarQube processing completed successfully");
              return;
            } else {
              String status = current.path("status").asText();
              if ("FAILED".equals(status) || "CANCELED".equals(status)) {
                throw new RuntimeException("SonarQube processing failed: " + status);
              } else if ("SUCCESS".equals(status)) {
                // Task completed successfully
                logger.info("SonarQube processing completed successfully");
                return;
              }
              logger.debug("SonarQube processing current task (status: {})...", status);
            }
          }
        } else {
          logger.warn(
              "Unexpected response from Compute Engine API: {} - {}",
              response.statusCode(),
              response.body());
        }
      } catch (IOException e) {
        logger.warn("Error polling SonarQube Compute Engine status: {}", e.getMessage());
      }

      // Wait before next poll
      Thread.sleep(POLLING_INTERVAL_MS);
    }
  }

  /**
   * Creates a basic authentication header value.
   *
   * @param username SonarQube username
   * @param password SonarQube password
   * @return Base64 encoded authentication header
   */
  private static String createAuthHeader(String username, String password) {
    String auth = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Adds common SonarQube properties to the command list.
   *
   * @param command The command list to add properties to
   * @param sonarUrl SonarQube URL
   * @param projectKey Project key
   * @param username SonarQube username
   * @param password SonarQube password
   * @param enableDiffblue Whether to enable Diffblue plugin
   */
  private static void addCommonSonarProperties(
      List<String> command,
      String sonarUrl,
      String projectKey,
      String username,
      String password,
      boolean enableDiffblue) {
    command.add("-Dsonar.host.url=" + sonarUrl);
    command.add("-Dsonar.login=" + username);
    command.add("-Dsonar.password=" + password);
    command.add("-Dsonar.projectKey=" + projectKey);

    if (enableDiffblue) {
      command.add("-Dsonar.diffblue.toggle=true");
    }
  }

  /**
   * Builds Maven command for SonarQube analysis.
   *
   * @param projectDir Project directory path
   * @param sonarUrl SonarQube URL
   * @param projectKey Project key
   * @param username SonarQube username
   * @param password SonarQube password
   * @param enableDiffblue Whether to enable Diffblue plugin
   * @return ProcessBuilder configured for Maven
   */
  private static ProcessBuilder buildMavenCommand(
      Path projectDir,
      String sonarUrl,
      String projectKey,
      String username,
      String password,
      boolean enableDiffblue) {
    List<String> command = new ArrayList<>();

    // Use mvn wrapper if available, otherwise system mvn
    command.add(getMavenExecutable(projectDir));
    command.add("clean");
    command.add("verify");
    command.add("sonar:sonar");

    addCommonSonarProperties(command, sonarUrl, projectKey, username, password, enableDiffblue);

    // Skip tests if they would fail (we're only interested in analysis)
    command.add("-DskipTests=false"); // Run tests to generate coverage
    command.add("-Dmaven.test.failure.ignore=true"); // Continue even if tests fail

    return new ProcessBuilder(command);
  }

  /**
   * Gets the Maven executable (wrapper if available, otherwise system mvn).
   *
   * @param projectDir Project directory to check for mvnw wrapper
   * @return Maven command
   */
  private static String getMavenExecutable(Path projectDir) {
    // Check if mvnw exists in the project directory
    if (Files.exists(projectDir.resolve("mvnw"))) {
      return "./mvnw";
    }
    return "mvn";
  }
}
