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

import java.io.File;
import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/** A utility that reads Jacoco XML files. */
public final class JacocoReader {

  private JacocoReader() {}

  /**
   * Reads a given Jacoco XML file and parses the relevant data into a map of filename :
   * FileCoverage.
   *
   * @param xmlFile the Jacoco XML to parse
   * @return a map of filename : FileCoverage
   * @throws Exception if there's an issue parsing the XML files
   */
  public static Map<String, FileCoverage> parse(File xmlFile) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    // Prevent loading external DTDs like "report.dtd"
    try {
      dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
      dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (Exception e) {
      // If some feature isn't supported, ignore; best-effort hardening
    }

    DocumentBuilder db = dbf.newDocumentBuilder();

    // EntityResolver that returns an empty DTD so the parser doesn't hit the filesystem
    db.setEntityResolver(
        new EntityResolver() {
          @Override
          public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(new StringReader(""));
          }
        });

    Document doc = db.parse(xmlFile);

    Map<String, FileCoverage> result = new HashMap<>();

    NodeList packageNodes = doc.getElementsByTagName("package");
    // for each package
    for (int i = 0; i < packageNodes.getLength(); i++) {
      Element pkg = (Element) packageNodes.item(i);
      // i.e. org/example, but can be empty if at root
      String pkgName = pkg.getAttribute("name");

      // for each source file in this package
      NodeList sourceFileNodes = pkg.getElementsByTagName("sourcefile");
      for (int j = 0; j < sourceFileNodes.getLength(); j++) {
        Element sf = (Element) sourceFileNodes.item(j);
        String fileName = sf.getAttribute("name"); // i.e. MyClass.java

        String key = (pkgName != "" ? pkgName + "/" : "") + fileName;

        FileCoverage fc = new FileCoverage();
        fc.pkg = pkgName;
        fc.file = fileName;
        fc.coverable = new HashSet<>();
        fc.covered = new HashSet<>();

        NodeList lineNodes = sf.getElementsByTagName("line");
        for (int k = 0; k < lineNodes.getLength(); k++) {
          Element ln = (Element) lineNodes.item(k);
          // line number
          int line = Integer.parseInt(ln.getAttribute("nr"));
          // covered instructions
          int ci = Integer.parseInt(ln.getAttribute("ci"));
          // missed instructions
          int mi = Integer.parseInt(ln.getAttribute("mi"));

          // get "coverableLinesCount" and "covered" line counts
          if (ci + mi > 0) {
            // add to "coverableLinesCount" count
            fc.coverable.add(line);

            if (ci > 0) {
              // if at least part of the line is covered, add to "covered" lines
              fc.covered.add(line);
            }
          }
        }

        result.put(key, fc);
      }
    }

    return result;
  }

  /** Represents coverage information for a file (source code class). */
  public static class FileCoverage {

    public String pkg;

    public String file;

    public Set<Integer> coverable;

    public Set<Integer> covered;
  }
}
