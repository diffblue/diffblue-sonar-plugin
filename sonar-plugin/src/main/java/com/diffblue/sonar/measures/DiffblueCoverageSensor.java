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
package com.diffblue.sonar.measures;

import com.diffblue.core.CoverageModelBuilder;
import com.diffblue.core.model.CoverageModel;
import com.diffblue.sonar.config.DiffblueProperties;
import java.io.File;
import java.util.Map;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * From Sonar's docs: A Sensor is invoked once for each module of a project, starting from leaf
 * modules. This sensor computes and stores coverage metrics on individual files in the project.
 */
public class DiffblueCoverageSensor implements Sensor {

  private static final Logger LOG = Loggers.get(DiffblueCoverageSensor.class);

  public static final String DEFAULT_DIFFBLUE_JACOCO_PATH =
      ".diffblue/reports/diffblue-tests-jacoco-report.xml";

  public static final String DEFAULT_MANUAL_JACOCO_PATH =
      ".diffblue/reports/manual-tests-jacoco-report.xml";

  private final Configuration config;

  public DiffblueCoverageSensor(Configuration config) {
    this.config = config;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    // disable execution of sensor if project does not contain Java files
    descriptor.name("Diffblue Coverage Sensor").onlyOnLanguage("java");
  }

  @Override
  public void execute(SensorContext context) {
    // Check if the sensor is configured to be on/off
    boolean enabled =
        config
            .getBoolean(DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY)
            // ensure that toggle is off unless enabled in config
            .orElse(false);

    if (!enabled) {
      LOG.debug(
          "Diffblue Coverage plugin: sensor will not run as the Diffblue Coverage plugin is "
              + "disabled for project {}",
          context.project().key());
      return;
    }
    LOG.info(
        "Diffblue Coverage plugin: sensor starting analysis for module in project {}",
        context.project().key());

    File baseDir = context.fileSystem().baseDir();

    File diffblueJacocoFile = new File(baseDir, DEFAULT_DIFFBLUE_JACOCO_PATH);
    File manualJacocoFile = new File(baseDir, DEFAULT_MANUAL_JACOCO_PATH);

    LOG.debug(
        "Diffblue Coverage plugin: sensor looking for input files {}, {}",
        diffblueJacocoFile.getAbsolutePath(),
        manualJacocoFile.getAbsolutePath());

    if (!diffblueJacocoFile.isFile() || !manualJacocoFile.isFile()) {
      LOG.warn(
          "Diffblue Coverage plugin: one or more required input files are missing, skipping sensor execution. "
              + "diff='{}', manual='{}'",
          diffblueJacocoFile.getAbsolutePath(),
          manualJacocoFile.getAbsolutePath());
      return;
    }

    Map<String, CoverageModel> coverageModelMap;
    try {
      // build the coverage model for each file in this module
      coverageModelMap =
          CoverageModelBuilder.buildPerFileCoverageModel(diffblueJacocoFile, manualJacocoFile);
    } catch (Exception e) {
      LOG.error(
          "Diffblue Coverage plugin: sensor failed to compute coverage model from input files. "
              + "No Diffblue measures will be stored",
          e);
      return;
    }

    if (coverageModelMap != null) {
      FileSystem fs = context.fileSystem();

      // get all .java source files in the current module
      FilePredicates p = fs.predicates();
      Iterable<InputFile> inputFiles =
          fs.inputFiles(p.and(p.hasType(InputFile.Type.MAIN), p.hasLanguage("java")));

      for (InputFile file : inputFiles) {
        // get the coverage model for this file
        CoverageModel fileCoverageModel =
            findCoverageModelForInputFile(file.key(), coverageModelMap);

        // save measures for this file
        if (fileCoverageModel != null) {
          saveFileIntMeasure(
              context,
              file,
              DiffblueMetrics.LINES_COVERABLE,
              fileCoverageModel.getCoverableLinesCount());
          saveFileIntMeasure(
              context,
              file,
              DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY,
              fileCoverageModel.getCoveredByDiffblueOnlyCount());
          saveFileIntMeasure(
              context,
              file,
              DiffblueMetrics.LINES_COVERED_DIFFBLUE,
              fileCoverageModel.getCoveredByDiffblueCount());
        } else {
          // TODO other error handling?
          LOG.debug(
              "Diffblue Coverage plugin: could not find coverage model for input file key {}, will not store "
                  + "Diffblue measures for this file",
              file.key());
        }
      }
    }
  }

  private static void saveFileIntMeasure(
      SensorContext ctx, InputFile inputFile, Metric metric, int value) {
    ctx.newMeasure().forMetric(metric).on(inputFile).withValue(value).save();
  }

  /**
   * Find the corresponding CoverageModel record for the input file. The inputFileKey is in the
   * format "Multi-Module-CF-Project:subprojectB/submodule1/src/main/java/org/example/ClassB.java"
   * and the coverageModelMap key is in the format "org/example/ClassB.java".
   *
   * <p>This is a simple implementation that would be slow for large modules. Could improve in the
   * future.
   *
   * @param inputFileKey the {@link InputFile} key
   * @param coverageModelMap a map of class name : CoverageModel
   * @return the matching {@link CoverageModel} if there is one; otherwise null
   */
  private CoverageModel findCoverageModelForInputFile(
      String inputFileKey, Map<String, CoverageModel> coverageModelMap) {
    // we know we're in the right project and module, so just see if the package and
    // class match any entries
    String matchingKey =
        coverageModelMap.keySet().stream()
            .filter(k -> inputFileKey.endsWith(k))
            .findFirst()
            .orElse(null);
    if (matchingKey != null) {
      return coverageModelMap.get(matchingKey);
    } else {
      return null;
    }
  }
}
