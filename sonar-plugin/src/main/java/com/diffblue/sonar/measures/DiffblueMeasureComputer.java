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

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Aggregates the following measures computed per-file into higher-level summaries (module,
 * project):
 * <li>LINES_COVERABLE
 * <li>LINES_COVERED_DIFFBLUE_ONLY
 * <li>LINES_COVERED_DIFFBLUE
 *
 *     <p>Runs server-side.
 */
public class DiffblueMeasureComputer implements MeasureComputer {

  private static final Logger LOG = Loggers.get(DiffblueMeasureComputer.class);

  @Override
  public MeasureComputerDefinition define(
      MeasureComputerDefinitionContext measureComputerDefinitionContext) {
    return measureComputerDefinitionContext
        .newDefinitionBuilder()
        .setInputMetrics(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key())
        .setOutputMetrics(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key())
        .build();
  }

  @Override
  public void compute(MeasureComputerContext context) {
    // check if the plugin is enabled for the current project
    boolean enabled =
        Boolean.parseBoolean(
            context.getSettings().getString(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY));
    if (!enabled) {
      if (context.getComponent().getType().equals(Component.Type.PROJECT)) {
        // only log this message once for the project instead of for each
        // component
        LOG.debug(
            "Diffblue Coverage plugin: Diffblue Measure Computer will not run as the Diffblue "
                + "Coverage plugin is disabled for the project {}",
            context.getComponent().getKey());
      }
      return;
    }

    // skip Files (they already have data from the Sensor)
    if (context.getComponent().getType() == Component.Type.FILE) {
      return;
    }

    // sum measures from immediate children
    sumChildIntMeasures(context, LINES_COVERABLE.key());
    sumChildIntMeasures(context, LINES_COVERED_DIFFBLUE_ONLY.key());
    sumChildIntMeasures(context, LINES_COVERED_DIFFBLUE.key());
  }

  private void sumChildIntMeasures(MeasureComputerContext context, String measureKey) {
    int sum = 0;
    boolean hasChildrenWithMeasures = false;
    for (Measure childMeasure : context.getChildrenMeasures(measureKey)) {
      sum += childMeasure.getIntValue();
      hasChildrenWithMeasures = true;
    }

    // save the sum to the current component (Directory or Project)
    // this 'feeds' the parent component when the MeasureComputer runs on it next
    if (hasChildrenWithMeasures) {
      LOG.trace(
          "Diffblue Coverage plugin: Diffblue Measure Computer adding measure {} at context {} with value {}",
          measureKey,
          context.getComponent().getType(),
          sum);
      context.addMeasure(measureKey, sum);
    }
  }
}
