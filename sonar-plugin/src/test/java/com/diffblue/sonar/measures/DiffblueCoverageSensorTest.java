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

import static com.diffblue.sonar.config.DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.diffblue.core.CoverageModelBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.config.internal.MapSettings;

public class DiffblueCoverageSensorTest {

  @Test
  void testExecute() {
    // set src/test/resources/testProject as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "testProject");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);
    MapSettings settings = new MapSettings();
    settings.setProperty(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");
    context.setSettings(settings);

    // add a java file for the sensor to process
    InputFile inputFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/ClassC.java")
            .setLanguage("java")
            .initMetadata(
                "package org.example;\n"
                    + "\n"
                    + "public class ClassC {\n"
                    + "\n"
                    + "    public String hello() {\n"
                    + "        return \"Hello\";\n"
                    + "    }\n"
                    + "}")
            .build();
    context.fileSystem().add(inputFile);

    // execute the sensor
    DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
    sensor.execute(context);

    // verify measures stored
    Measure<Integer> measure =
        context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERABLE.key());
    assertNotNull(measure);
    assertEquals(2, measure.value());

    measure = context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key());
    assertNotNull(measure);
    assertEquals(2, measure.value());

    measure = context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key());
    assertNotNull(measure);
    assertEquals(2, measure.value());
  }

  /** Test that the sensor only runs on java source files. */
  @Test
  void testExecuteOnlyOnJavaFiles() {
    // set src/test/resources/testProject as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "testProject");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);

    // add a package-info file (example of a file that should not be processed by the
    // sensor)
    InputFile ignoredFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/package-info.java")
            .setLanguage("java")
            .initMetadata(
                "/**\n" + "* A comment about this package.\n" + "*/\n" + "package org.example;")
            .build();
    context.fileSystem().add(ignoredFile);

    // execute the sensor
    DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
    sensor.execute(context);

    // verify NO measures are stored for this file
    assertNull(context.measure(ignoredFile.key(), DiffblueMetrics.LINES_COVERABLE.key()));
    assertNull(
        context.measure(ignoredFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.measure(ignoredFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key()));
    assertNull(context.lineHits(ignoredFile.key(), 3));
    assertNull(context.lineHits(ignoredFile.key(), 6));
  }

  @Test
  void testExecuteToggleOff() {
    // set src/test/resources/testProject as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "testProject");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);
    MapSettings settings = new MapSettings();
    settings.setProperty(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "false");
    context.setSettings(settings);

    // add a java file for the sensor to process
    InputFile inputFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/ClassC.java")
            .setLanguage("java")
            .initMetadata(
                "package org.example;\n"
                    + "\n"
                    + "public class ClassC {\n"
                    + "\n"
                    + "    public String hello() {\n"
                    + "        return \"Hello\";\n"
                    + "    }\n"
                    + "}")
            .build();
    context.fileSystem().add(inputFile);

    // execute the sensor
    DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
    sensor.execute(context);

    // verify sensor has not recorded any measures
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERABLE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.lineHits(inputFile.key(), 3));
    assertNull(context.lineHits(inputFile.key(), 6));
  }

  @Test
  void testExecuteToggleDefault() {
    // set src/test/resources/testProject as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "testProject");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);
    MapSettings settings = new MapSettings();
    context.setSettings(settings);

    // add a java file for the sensor to process
    InputFile inputFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/ClassC.java")
            .setLanguage("java")
            .initMetadata(
                "package org.example;\n"
                    + "\n"
                    + "public class ClassC {\n"
                    + "\n"
                    + "    public String hello() {\n"
                    + "        return \"Hello\";\n"
                    + "    }\n"
                    + "}")
            .build();
    context.fileSystem().add(inputFile);

    // execute the sensor
    DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
    sensor.execute(context);

    // verify sensor has not recorded any measures
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERABLE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.lineHits(inputFile.key(), 3));
    assertNull(context.lineHits(inputFile.key(), 6));
  }

  /**
   * Test that when one or more of the required input files are not present, the sensor does not
   * store any measures.
   */
  @Test
  void testExecuteOneOrMoreFilesMissing() {
    // set src/test/resources/projectWithoutInputFiles as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "projectWithoutInputFiles");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);
    MapSettings settings = new MapSettings();
    settings.setProperty(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");
    context.setSettings(settings);

    // add a java file for the sensor to process
    InputFile inputFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/ClassC.java")
            .setLanguage("java")
            .initMetadata(
                "package org.example;\n"
                    + "\n"
                    + "public class ClassC {\n"
                    + "\n"
                    + "    public String hello() {\n"
                    + "        return \"Hello\";\n"
                    + "    }\n"
                    + "}")
            .build();
    context.fileSystem().add(inputFile);

    // execute the sensor
    DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
    sensor.execute(context);

    // the sensor should not execute as one or more of the required files are missing
    // verify NO measures are stored for this file
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERABLE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.lineHits(inputFile.key(), 3));
    assertNull(context.lineHits(inputFile.key(), 6));
  }

  /**
   * Test that when there's an exception thrown in CoverageModelBuilder.buildPerFileCoverageModel,
   * the sensor does not store any measures.
   */
  @Test
  void testExecuteErrorBuildingCoverageModel() {
    // set src/test/resources/testProject as the base
    Path resourceDirectory = Paths.get("src", "test", "resources", "testProject");
    SensorContextTester context = SensorContextTester.create(resourceDirectory);
    MapSettings settings = new MapSettings();
    settings.setProperty(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");
    context.setSettings(settings);

    // add a java file for the sensor to process
    InputFile inputFile =
        new TestInputFileBuilder("testProject", "src/main/java/org/example/ClassC.java")
            .setLanguage("java")
            .initMetadata(
                "package org.example;\n"
                    + "\n"
                    + "public class ClassC {\n"
                    + "\n"
                    + "    public String hello() {\n"
                    + "        return \"Hello\";\n"
                    + "    }\n"
                    + "}")
            .build();
    context.fileSystem().add(inputFile);

    // mock throwing an exception in CoverageModelBuilder.buildPerFileCoverageModel
    try (MockedStatic<CoverageModelBuilder> mocked = mockStatic(CoverageModelBuilder.class)) {
      mocked
          .when(() -> CoverageModelBuilder.buildPerFileCoverageModel(any(), any()))
          .thenThrow(new RuntimeException("Test Exception"));

      // execute the sensor
      DiffblueCoverageSensor sensor = new DiffblueCoverageSensor(context.config());
      sensor.execute(context);
    }

    // verify NO measures are stored for this file as the coverage model builder failed
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERABLE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE.key()));
    assertNull(context.measure(inputFile.key(), DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.lineHits(inputFile.key(), 3));
    assertNull(context.lineHits(inputFile.key(), 6));
  }
}
