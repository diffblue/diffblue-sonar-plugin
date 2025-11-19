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
package com.diffblue.core.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JacocoReaderTest {

  @Test
  void testParseSingleModule() throws Exception {
    Map<String, JacocoReader.FileCoverage> jacocoMap =
        JacocoReader.parse(
            new File("src/test/resources/singleModule/diffblue-tests-jacoco-report.xml"));

    // check there's one entry per source code file
    assertEquals(23, jacocoMap.size());

    // check the key format
    assertTrue(jacocoMap.containsKey("org/springframework/samples/petclinic/vet/Vet.java"));
  }

  @Test
  void testParseMultiModuleDiffblue() throws Exception {
    Map<String, JacocoReader.FileCoverage> jacocoMap =
        JacocoReader.parse(
            new File(
                "src/test/resources/multiModule/subprojectB/submodule1/diffblue-tests-jacoco-report.xml"));

    // check there's one entry per source code file
    assertEquals(1, jacocoMap.size());

    // check the keys and key format
    assertTrue(jacocoMap.containsKey("org/example/ClassB.java"));

    JacocoReader.FileCoverage coverage = jacocoMap.get("org/example/ClassB.java");
    assertEquals("org/example", coverage.pkg);
    assertEquals("ClassB.java", coverage.file);
    Set<Integer> expectedCoverable = Set.of(3, 6);
    assertEquals(expectedCoverable.size(), coverage.coverable.size());
    assertTrue(
        coverage.coverable.containsAll(expectedCoverable)
            && expectedCoverable.containsAll(coverage.coverable));
    Set<Integer> expectedCovered = Set.of(3, 6);
    assertEquals(expectedCovered.size(), coverage.covered.size());
    assertTrue(
        coverage.covered.containsAll(expectedCovered)
            && expectedCovered.containsAll(coverage.covered));
  }

  @Test
  void testParseMultiModuleManual() throws Exception {
    Map<String, JacocoReader.FileCoverage> jacocoMap =
        JacocoReader.parse(
            new File("src/test/resources/multiModule/subprojectA/manual-tests-jacoco-report.xml"));

    // check there's one entry per source code file
    assertEquals(2, jacocoMap.size());

    // check the keys and key format
    assertTrue(jacocoMap.containsKey("ClassA.java"));
    assertTrue(jacocoMap.containsKey("MyClass.java"));

    // check one entry
    JacocoReader.FileCoverage coverage = jacocoMap.get("MyClass.java");
    assertEquals("", coverage.pkg);
    assertEquals("MyClass.java", coverage.file);
    Set<Integer> expectedCoverable =
        Set.of(35, 39, 11, 43, 12, 14, 15, 47, 16, 19, 51, 23, 55, 27, 59, 31, 63);
    assertEquals(expectedCoverable.size(), coverage.coverable.size());
    assertTrue(
        coverage.coverable.containsAll(expectedCoverable)
            && expectedCoverable.containsAll(coverage.coverable));
    Set<Integer> expectedCovered = Set.of(16, 14, 15, 31);
    assertEquals(expectedCovered.size(), coverage.covered.size());
    assertTrue(
        coverage.covered.containsAll(expectedCovered)
            && expectedCovered.containsAll(coverage.covered));
  }
}
