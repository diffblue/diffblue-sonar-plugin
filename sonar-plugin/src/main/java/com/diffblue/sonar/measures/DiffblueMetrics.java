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

import java.util.Arrays;
import java.util.List;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.measures.Metrics;

/** Diffblue metrics to be stored in SonarQube */
public class DiffblueMetrics implements Metrics {

  // the section in the UI where the metric appears
  public static final String DOMAIN = "Diffblue";

  public static final Metric<Integer> LINES_COVERABLE =
      intMetric("diffblue.lines.coverable", "Coverable Lines");

  public static final Metric<Integer> LINES_COVERED_DIFFBLUE_ONLY =
      intMetric("diffblue.lines.covered.diffblue_only", "Lines Covered by Diffblue (exclusively)");

  public static final Metric<Integer> LINES_COVERED_DIFFBLUE =
      intMetric("diffblue.lines.covered.diffblue", "Lines Covered by Diffblue (total)");

  public static final Metric<Double> PERCENT_COVERED_DIFFBLUE_ONLY =
      percentMetric(
          "diffblue.percent.covered.diffblue_only", "Coverage from Diffblue (exclusively)");

  public static final Metric<Double> PERCENT_COVERED_DIFFBLUE =
      percentMetric("diffblue.percent.covered.diffblue", "Coverage from Diffblue (total)");

  private static Metric<Integer> intMetric(String key, String displayName) {
    return new Metric.Builder(key, displayName, ValueType.INT)
        .setDomain(DOMAIN)
        .setDirection(Metric.DIRECTION_BETTER)
        .create();
  }

  private static Metric<Double> percentMetric(String key, String displayName) {
    return new Metric.Builder(key, displayName, ValueType.PERCENT)
        .setDomain(DOMAIN)
        .setDirection(Metric.DIRECTION_BETTER)
        .setBestValue(100.0)
        .setWorstValue(0.0)
        .setDecimalScale(1) // one decimal place precision
        .create();
  }

  /**
   * All metrics must be included here in order to be stored!
   *
   * @return a list of all metrics defined
   */
  @Override
  public List<Metric> getMetrics() {
    return Arrays.asList(
        LINES_COVERABLE,
        LINES_COVERED_DIFFBLUE_ONLY,
        LINES_COVERED_DIFFBLUE,
        PERCENT_COVERED_DIFFBLUE_ONLY,
        PERCENT_COVERED_DIFFBLUE);
  }
}
