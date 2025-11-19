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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

class DiffbluePropertiesDiffblueTest {
  /**
   * Test {@link DiffblueProperties#definitions()}.
   *
   * <p>Method under test: {@link DiffblueProperties#definitions()}
   */
  @Test
  @DisplayName("Test definitions()")
  @Tag("MaintainedByDiffblue")
  void testDefinitions() {
    // Arrange and Act
    List<PropertyDefinition> actualDefinitionsResult = DiffblueProperties.definitions();

    // Assert
    assertEquals(1, actualDefinitionsResult.size());
    PropertyDefinition getResult = actualDefinitionsResult.get(0);
    assertEquals("", getResult.deprecatedKey());
    assertEquals("", getResult.subCategory());
    assertEquals(1, getResult.qualifiers().size());
    assertEquals(1, getResult.index());
    assertEquals(PropertyType.BOOLEAN, getResult.type());
    assertFalse(getResult.global());
    assertFalse(getResult.multiValues());
    assertTrue(getResult.fields().isEmpty());
    assertTrue(getResult.options().isEmpty());
    String expectedDefaultValueResult = Boolean.FALSE.toString();
    assertEquals(expectedDefaultValueResult, getResult.defaultValue());
    assertEquals(DiffblueProperties.CATEGORY, getResult.category());
    assertEquals(
        DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_DESCRIPTION, getResult.description());
    assertEquals(DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_NAME, getResult.name());
    assertEquals(DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, getResult.key());
    assertEquals(DiffblueProperties.SONAR_DIFFBLUE_PLUGIN_TOGGLE_PROPERTY, getResult.toString());
  }
}
