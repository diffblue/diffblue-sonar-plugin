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
package com.diffblue.core.model;

/** Holds statistics about Diffblue and manual coverage for a file, module, or project. */
public class CoverageModel {

  private int coverableLinesCount; // union of coverable lines

  private int coveredByDiffblueOnlyCount;

  private int coveredByDiffblueCount;

  public int getCoverableLinesCount() {
    return coverableLinesCount;
  }

  public void setCoverableLinesCount(int coverableLinesCount) {
    this.coverableLinesCount = coverableLinesCount;
  }

  public int getCoveredByDiffblueOnlyCount() {
    return coveredByDiffblueOnlyCount;
  }

  public void setCoveredByDiffblueOnlyCount(int coveredByDiffblueOnlyCount) {
    this.coveredByDiffblueOnlyCount = coveredByDiffblueOnlyCount;
  }

  public int getCoveredByDiffblueCount() {
    return coveredByDiffblueCount;
  }

  public void setCoveredByDiffblueCount(int coveredByDiffblueCount) {
    this.coveredByDiffblueCount = coveredByDiffblueCount;
  }
}
