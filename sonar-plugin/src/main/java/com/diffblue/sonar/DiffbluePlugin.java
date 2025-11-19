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
package com.diffblue.sonar;

import com.diffblue.sonar.config.DiffblueProperties;
import com.diffblue.sonar.measures.DiffblueCoverageSensor;
import com.diffblue.sonar.measures.DiffblueMeasureComputer;
import com.diffblue.sonar.measures.DiffblueMetrics;
import com.diffblue.sonar.measures.PercentageMeasureComputer;
import org.sonar.api.Plugin;

/** The entry point for the plugin. */
public class DiffbluePlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(
        DiffblueMetrics.class,
        DiffblueCoverageSensor.class,
        DiffblueMeasureComputer.class,
        PercentageMeasureComputer.class);

    context.addExtensions(DiffblueProperties.definitions());
  }
}
