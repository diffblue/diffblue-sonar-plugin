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
package com.diffblue.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.diffblue.core.model.CoverageModel;
import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class CoverageModelBuilderTest {

  @Test
  void testBuildPerFileCoverageModel() throws Exception {
    Map<String, CoverageModel> coverageModelMap =
        CoverageModelBuilder.buildPerFileCoverageModel(
            new File("src/test/resources/singleModule/diffblue-tests-jacoco-report.xml"),
            new File("src/test/resources/singleModule/manual-tests-jacoco-report.xml"));

    // check that we have coverage for all the expected classes
    assertEquals(23, coverageModelMap.size());

    // check one entry
    assertTrue(coverageModelMap.containsKey("org/springframework/samples/petclinic/vet/Vet.java"));
    CoverageModel vetCoverageModel =
        coverageModelMap.get("org/springframework/samples/petclinic/vet/Vet.java");
    assertEquals(12, vetCoverageModel.getCoverableLinesCount());
    assertEquals(11, vetCoverageModel.getCoveredByDiffblueOnlyCount());
    assertEquals(12, vetCoverageModel.getCoveredByDiffblueCount());

    // check that the sum of all the file stats match the overall expected values
    assertEquals(
        270,
        coverageModelMap.values().stream()
            .map(c -> c.getCoverableLinesCount())
            .collect(Collectors.summingInt(Integer::intValue)));
    assertEquals(
        242,
        coverageModelMap.values().stream()
            .map(c -> c.getCoveredByDiffblueCount())
            .collect(Collectors.summingInt(Integer::intValue)));
    assertEquals(
        230,
        coverageModelMap.values().stream()
            .map(c -> c.getCoveredByDiffblueOnlyCount())
            .collect(Collectors.summingInt(Integer::intValue)));
  }
}
