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
package com.diffblue.sonar.config;

import java.util.List;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

/** Define properties the user can set for the plugin. */
public final class DiffblueProperties {

  public static final String CATEGORY = "Diffblue";

  public static final String KEY_BASE = "sonar.diffblue";

  public static final String SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY = KEY_BASE + ".toggle";

  public static final String SONAR_DIFFBLUE_PLUGIN_TOGGLE_NAME = "Enable Diffblue SonarQube Plugin";

  public static final String SONAR_DIFFBLUE_PLUGIN_TOGGLE_DESCRIPTION =
      "Toggle Diffblue coverage analysis on or off";

  private DiffblueProperties() {
    // utility class
  }

  public static List<PropertyDefinition> definitions() {
    return List.of(
        PropertyDefinition.builder(SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY)
            .name(SONAR_DIFFBLUE_PLUGIN_TOGGLE_NAME)
            .description(SONAR_DIFFBLUE_PLUGIN_TOGGLE_DESCRIPTION)
            .category(CATEGORY)
            .index(1)
            .type(PropertyType.BOOLEAN)
            .defaultValue(Boolean.toString(false))
            .onlyOnQualifiers(Qualifiers.PROJECT)
            .build());
  }
}
