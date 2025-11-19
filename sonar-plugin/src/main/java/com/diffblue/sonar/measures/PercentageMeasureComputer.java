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
import static com.diffblue.sonar.measures.DiffblueMetrics.PERCENT_COVERED_DIFFBLUE;
import static com.diffblue.sonar.measures.DiffblueMetrics.PERCENT_COVERED_DIFFBLUE_ONLY;

import java.util.stream.StreamSupport;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * Reads in LINES_COVERABLE, LINES_COVERED_DIFFBLUE_ONLY, and LINES_COVERED_DIFFBLUE to calculate
 * and store percentage values representing coverage.
 *
 * <p>This will run after DiffblueMeasureComputer as it depends on metrics output by that computer.
 */
public class PercentageMeasureComputer implements MeasureComputer {

  private static final Logger LOG = Loggers.get(PercentageMeasureComputer.class);

  @Override
  public MeasureComputerDefinition define(
      MeasureComputerDefinitionContext measureComputerDefinitionContext) {
    return measureComputerDefinitionContext
        .newDefinitionBuilder()
        .setInputMetrics(
            LINES_COVERABLE.key(), LINES_COVERED_DIFFBLUE_ONLY.key(), LINES_COVERED_DIFFBLUE.key())
        .setOutputMetrics(PERCENT_COVERED_DIFFBLUE_ONLY.key(), PERCENT_COVERED_DIFFBLUE.key())
        .build();
  }

  @Override
  public void compute(MeasureComputerContext context) {
    boolean enabled =
        Boolean.parseBoolean(
            context.getSettings().getString(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY));
    if (!enabled) {
      if (context.getComponent().getType().equals(Component.Type.PROJECT)) {
        // only log this message once for the project instead of for each component
        LOG.debug(
            "Diffblue Coverage plugin: Percentage Measure Computer will not run as the Diffblue "
                + "Coverage plugin is disabled for the project {}",
            context.getComponent().getKey());
      }
      return;
    }

    // get children measures (if any)
    int totalCoverable = sumChildren(context, LINES_COVERABLE.key());
    int totalDiffblueOnly = sumChildren(context, LINES_COVERED_DIFFBLUE_ONLY.key());
    int totalDiffblue = sumChildren(context, LINES_COVERED_DIFFBLUE.key());

    // add the current component's own measure (if it's a file that has data)
    Measure currentCoverable = context.getMeasure(LINES_COVERABLE.key());
    Measure currentDiffblueOnly = context.getMeasure(LINES_COVERED_DIFFBLUE_ONLY.key());
    Measure currentDiffblue = context.getMeasure(LINES_COVERED_DIFFBLUE.key());

    // calculate percentages based on children measures + current context measure
    if (currentCoverable != null) {
      totalCoverable += currentCoverable.getIntValue();
    }
    if (currentDiffblueOnly != null) {
      totalDiffblueOnly += currentDiffblueOnly.getIntValue();
    }
    if (currentDiffblue != null) {
      totalDiffblue += currentDiffblue.getIntValue();
    }

    if (totalCoverable > 0) { // don't divide by 0
      calculateAndStorePercentage(
          context, PERCENT_COVERED_DIFFBLUE_ONLY.key(), totalDiffblueOnly, totalCoverable);
      calculateAndStorePercentage(
          context, PERCENT_COVERED_DIFFBLUE.key(), totalDiffblue, totalCoverable);
    }
  }

  private int sumChildren(MeasureComputerContext context, String measureKey) {
    // getChildrenMeasures returns an Iterable<Measure>
    return StreamSupport.stream(context.getChildrenMeasures(measureKey).spliterator(), false)
        .mapToInt(Measure::getIntValue)
        .sum();
  }

  private void calculateAndStorePercentage(
      MeasureComputerContext context, String measureKey, int numerator, int denominator) {
    double value = ((double) numerator / denominator) * 100;
    LOG.trace(
        "Diffblue Coverage plugin: Percentage Measure Computer adding measure {} at context {} with value {}",
        measureKey,
        context.getComponent().getType(),
        value);
    context.addMeasure(measureKey, value);
  }
}
