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
import static com.diffblue.sonar.measures.DiffblueMetrics.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.test.TestComponent;
import org.sonar.api.ce.measure.test.TestMeasureComputerContext;
import org.sonar.api.ce.measure.test.TestMeasureComputerDefinitionContext;
import org.sonar.api.ce.measure.test.TestSettings;

public class PercentageMeasureComputerTest {

  PercentageMeasureComputer computer;

  MeasureComputer.MeasureComputerDefinition definition;

  @BeforeEach
  void setup() {
    computer = new PercentageMeasureComputer();
    definition = computer.define(new TestMeasureComputerDefinitionContext());
  }

  @Test
  void testDefine() {
    TestMeasureComputerDefinitionContext definitionContext =
        new TestMeasureComputerDefinitionContext();
    MeasureComputer.MeasureComputerDefinition def = computer.define(definitionContext);

    assertNotNull(def);
    assertThat(def.getInputMetrics())
        .containsOnly(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key());
    assertThat(def.getOutputMetrics())
        .containsOnly(PERCENT_COVERED_DIFFBLUE_ONLY.key(), PERCENT_COVERED_DIFFBLUE.key());
  }

  @Test
  void testComputeDirectory() {
    // enable the plugin
    TestSettings settings = new TestSettings();
    settings.setValue(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");

    // set up a test directory
    TestComponent component = new TestComponent("dir", Component.Type.DIRECTORY, null);
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, settings, definition);

    // add simulated children measures for the computer to read
    context.addChildrenMeasures(LINES_COVERABLE.key(), 10, 5);
    context.addChildrenMeasures(LINES_COVERED_DIFFBLUE_ONLY.key(), 2, 0);
    context.addChildrenMeasures(LINES_COVERED_DIFFBLUE.key(), 7, 4);

    // execute
    computer.compute(context);

    // SonarQube will limit this to the number of decimal places configured on the metric
    assertEquals(
        13.333333333333334,
        context.getMeasure(PERCENT_COVERED_DIFFBLUE_ONLY.key()).getDoubleValue());
    assertEquals(
        73.333333333333334, context.getMeasure(PERCENT_COVERED_DIFFBLUE.key()).getDoubleValue());
  }

  @Test
  void testComputeFile() {
    // enable the plugin
    TestSettings settings = new TestSettings();
    settings.setValue(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");

    // set up a test file
    TestComponent component =
        new TestComponent(
            "dir", Component.Type.FILE, new TestComponent.FileAttributesImpl("java", false));
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, settings, definition);

    // add current measures
    // no children measures to add as it's Component.Type.FILE
    context.addInputMeasure(LINES_COVERABLE.key(), 20);
    context.addInputMeasure(LINES_COVERED_DIFFBLUE_ONLY.key(), 10);
    context.addInputMeasure(LINES_COVERED_DIFFBLUE.key(), 15);

    // execute
    computer.compute(context);

    assertEquals(50.0, context.getMeasure(PERCENT_COVERED_DIFFBLUE_ONLY.key()).getDoubleValue());
    assertEquals(75.0, context.getMeasure(PERCENT_COVERED_DIFFBLUE.key()).getDoubleValue());
  }

  @Test
  void testToggleOff() {
    // disable the plugin
    TestSettings settings = new TestSettings();
    settings.setValue(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "false");

    // set up a test file
    TestComponent component =
        new TestComponent(
            "dir", Component.Type.FILE, new TestComponent.FileAttributesImpl("java", false));
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, settings, definition);

    // don't need to set up measures as there shouldn't be any

    // execute
    computer.compute(context);

    assertNull(context.getMeasure(PERCENT_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.getMeasure(PERCENT_COVERED_DIFFBLUE.key()));
  }

  @Test
  void testToggleDefault() {
    // set up a test file
    TestComponent component =
        new TestComponent(
            "dir", Component.Type.FILE, new TestComponent.FileAttributesImpl("java", false));
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, new TestSettings(), definition);

    // don't need to set up measures as there shouldn't be any

    // execute
    computer.compute(context);

    assertFalse(
        Boolean.parseBoolean(
            context.getSettings().getString(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY)));
    assertNull(context.getMeasure(PERCENT_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.getMeasure(PERCENT_COVERED_DIFFBLUE.key()));
  }
}
