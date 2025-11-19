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

import com.diffblue.core.io.JacocoReader;
import com.diffblue.core.model.CoverageModel;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builds a CoverageModel from:
 * <li>Diffblue-only JaCoCo XML
 * <li>Manual-only JaCoCo XML
 */
public final class CoverageModelBuilder {

  private CoverageModelBuilder() {
    // utility class
  }

  /**
   * Read and summarise statistics for a module given the diffblue-tests-jacoco-report.xml and
   * manual-tests-jacoco-report.xml.
   *
   * @param diffblueJacocoFile a module's diffblue-tests-jacoco-report.xml file
   * @param manualJacocoFile a module's manual-tests-jacoco-report.xml file
   * @return a map of file key : CoverageModel
   * @throws Exception if there's an issue parsing the Jacoco XML files
   */
  public static Map<String, CoverageModel> buildPerFileCoverageModel(
      File diffblueJacocoFile, File manualJacocoFile) throws Exception {
    // parse the jacoco XML files
    Map<String, JacocoReader.FileCoverage> diffblueMap = JacocoReader.parse(diffblueJacocoFile);
    Map<String, JacocoReader.FileCoverage> manualMap = JacocoReader.parse(manualJacocoFile);

    // union of all files seen in either report
    Set<String> allFiles = new HashSet<>();
    allFiles.addAll(diffblueMap.keySet());
    allFiles.addAll(manualMap.keySet());

    Map<String, CoverageModel> perFileCoverageMap = new HashMap<>();

    // iterate through each file's coverage data
    for (String fileKey : allFiles) { // fileKey = pkgName + "/" + fileName
      JacocoReader.FileCoverage diffblueFileCoverage = diffblueMap.get(fileKey);
      JacocoReader.FileCoverage manualFileCoverage = manualMap.get(fileKey);

      // this file's coverage
      CoverageModel fileCoverageModel = new CoverageModel();

      // coverable lines in this file
      Set<Integer> coverableLines = new HashSet<>();
      if (diffblueFileCoverage != null) {
        coverableLines.addAll(diffblueFileCoverage.coverable);
      }
      if (manualFileCoverage != null) {
        coverableLines.addAll(manualFileCoverage.coverable);
      }
      fileCoverageModel.setCoverableLinesCount(coverableLines.size());

      Set<Integer> linesCoveredByDiffblue =
          (diffblueFileCoverage != null) ? diffblueFileCoverage.covered : Collections.emptySet();
      fileCoverageModel.setCoveredByDiffblueCount(linesCoveredByDiffblue.size());

      Set<Integer> linesCoveredByManual =
          (manualFileCoverage != null) ? manualFileCoverage.covered : Collections.emptySet();

      // calculate Diffblue only
      Set<Integer> diffblueOnly = new HashSet<>(linesCoveredByDiffblue);
      diffblueOnly.removeAll(linesCoveredByManual);
      fileCoverageModel.setCoveredByDiffblueOnlyCount(diffblueOnly.size());

      // add to map
      perFileCoverageMap.put(fileKey, fileCoverageModel);
    }

    return perFileCoverageMap;
  }
}
