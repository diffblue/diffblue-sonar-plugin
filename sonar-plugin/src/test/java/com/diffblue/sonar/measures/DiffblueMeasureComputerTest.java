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
import static com.diffblue.sonar.measures.DiffblueMetrics.LINES_COVERABLE;
import static com.diffblue.sonar.measures.DiffblueMetrics.LINES_COVERED_DIFFBLUE;
import static com.diffblue.sonar.measures.DiffblueMetrics.LINES_COVERED_DIFFBLUE_ONLY;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.ce.measure.test.TestComponent;
import org.sonar.api.ce.measure.test.TestMeasureComputerContext;
import org.sonar.api.ce.measure.test.TestMeasureComputerDefinitionContext;
import org.sonar.api.ce.measure.test.TestSettings;

public class DiffblueMeasureComputerTest {

  DiffblueMeasureComputer computer;

  MeasureComputer.MeasureComputerDefinition definition;

  TestMeasureComputerDefinitionContext defContext;

  @BeforeEach
  void setup() {
    computer = new DiffblueMeasureComputer();
    defContext = new TestMeasureComputerDefinitionContext();
    definition = computer.define(defContext);
  }

  @Test
  void testDefine() {
    assertNotNull(definition);
    assertThat(definition.getInputMetrics())
        .containsOnly(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key());
    assertThat(definition.getOutputMetrics())
        .containsOnly(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key());
  }

  @Test
  void testCompute() {
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

    // check that children measures have been summed
    assertEquals(15, context.getMeasure(LINES_COVERABLE.key()).getIntValue());
    assertEquals(2, context.getMeasure(LINES_COVERED_DIFFBLUE_ONLY.key()).getIntValue());
    assertEquals(11, context.getMeasure(LINES_COVERED_DIFFBLUE.key()).getIntValue());
  }

  @Test
  void testComputeNoMeasuresStoredForFile() {
    MeasureComputer.MeasureComputerContext context =
        mock(MeasureComputer.MeasureComputerContext.class);
    Component component = mock(Component.class);
    when(context.getComponent()).thenReturn(component);
    when(context.getComponent().getType()).thenReturn(Component.Type.FILE);

    // enable the plugin
    TestSettings settings = new TestSettings();
    settings.setValue(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "true");
    when(context.getSettings()).thenReturn(settings);

    computer.compute(context);

    // verify that addMeasure() was never called for any measures
    verify(context, never()).addMeasure(anyString(), anyInt());
    verify(context, never()).addMeasure(anyString(), anyDouble());
  }

  @Test
  void testToggleOff() {
    // disable the plugin
    TestSettings settings = new TestSettings();
    settings.setValue(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, "false");

    // set up a test directory
    TestComponent component = new TestComponent("dir", Component.Type.DIRECTORY, null);
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, settings, definition);

    // execute
    computer.compute(context);

    // the measure computer shouldn't have run/stored any measures since the plugin is disabled
    assertNull(context.getMeasure(LINES_COVERABLE.key()));
    assertNull(context.getMeasure(LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.getMeasure(LINES_COVERED_DIFFBLUE.key()));
  }

  @Test
  void testToggleDefault() {
    // set up a test directory
    TestComponent component = new TestComponent("dir", Component.Type.DIRECTORY, null);
    TestMeasureComputerContext context =
        new TestMeasureComputerContext(component, new TestSettings(), definition);

    // don't need to set up measures as there shouldn't be any

    // execute
    computer.compute(context);

    // the measure computer shouldn't have run/stored any measures since the plugin is disabled
    assertNull(context.getMeasure(LINES_COVERABLE.key()));
    assertNull(context.getMeasure(LINES_COVERED_DIFFBLUE_ONLY.key()));
    assertNull(context.getMeasure(LINES_COVERED_DIFFBLUE.key()));
  }
}
