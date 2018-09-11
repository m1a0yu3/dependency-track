/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.dependencytrack.parser.nvd;

import alpine.event.framework.Event;
import alpine.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.dependencytrack.event.IndexEvent;
import org.dependencytrack.model.Cwe;
import org.dependencytrack.model.Vulnerability;
import org.dependencytrack.persistence.QueryManager;
import us.springett.cvss.Cvss;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Parser and processor of NVD data feeds.
 *
 * @author Steve Springett
 * @since 3.0.0
 */
public final class NvdParser {

    private static final Logger LOGGER = Logger.getLogger(NvdParser.class);

    public void parse(File file) {
        if (!file.getName().endsWith(".json")) {
            return;
        }

        LOGGER.info("Parsing " + file.getName());

        try (QueryManager qm = new QueryManager();
             InputStream in = new FileInputStream(file)) {

            final JsonReader reader = Json.createReader(in);

            final JsonObject root = reader.readObject();
            final JsonArray cveItems = root.getJsonArray("CVE_Items");
            for (int i = 0; i < cveItems.size(); i++) {
                final Vulnerability vulnerability = new Vulnerability();
                vulnerability.setSource(Vulnerability.Source.NVD);

                final JsonObject cveItem = cveItems.getJsonObject(i);

                // CVE ID
                final JsonObject cve = cveItem.getJsonObject("cve");
                final JsonObject meta0 = cve.getJsonObject("CVE_data_meta");
                final JsonString meta1 = meta0.getJsonString("ID");
                vulnerability.setVulnId(meta1.getString());

                // CVE Published and Modified dates
                final String publishedDateString = cveItem.getString("publishedDate");
                final String lastModifiedDateString = cveItem.getString("lastModifiedDate");
                try {
                    if (StringUtils.isNotBlank(publishedDateString)) {
                        vulnerability.setPublished(Date.from(OffsetDateTime.parse(publishedDateString).toInstant()));
                    }
                    if (StringUtils.isNotBlank(lastModifiedDateString)) {
                        vulnerability.setUpdated(Date.from(OffsetDateTime.parse(lastModifiedDateString).toInstant()));
                    }
                } catch (DateTimeParseException | NullPointerException | IllegalArgumentException e) {
                    LOGGER.error("Unable to parse dates from NVD data feed", e);
                }

                // CVE Description
                final JsonObject descO = cve.getJsonObject("description");
                final JsonArray desc1 = descO.getJsonArray("description_data");
                final StringBuilder descriptionBuilder = new StringBuilder();
                for (int j = 0; j < desc1.size(); j++) {
                    final JsonObject desc2 = desc1.getJsonObject(j);
                    if ("en".equals(desc2.getString("lang"))) {
                        descriptionBuilder.append(desc2.getString("value"));
                        if (j < desc1.size() - 1) {
                            descriptionBuilder.append("\n\n");
                        }
                    }
                }
                vulnerability.setDescription(descriptionBuilder.toString());

                // CVE Impact
                parseCveImpact(cveItem, vulnerability);

                // CWE
                final JsonObject prob0 = cve.getJsonObject("problemtype");
                final JsonArray prob1 = prob0.getJsonArray("problemtype_data");
                for (int j = 0; j < prob1.size(); j++) {
                    final JsonObject prob2 = prob1.getJsonObject(j);
                    final JsonArray prob3 = prob2.getJsonArray("description");
                    for (int k = 0; k < prob3.size(); k++) {
                        final JsonObject prob4 = prob3.getJsonObject(k);
                        if ("en".equals(prob4.getString("lang"))) {
                            //vulnerability.setCwe(prob4.getString("value"));
                            final String cweString = prob4.getString("value");
                            if (cweString != null && cweString.startsWith("CWE-")) {
                                try {
                                    final int cweId = Integer.parseInt(cweString.substring(4, cweString.length()).trim());
                                    final Cwe cwe = qm.getCweById(cweId);
                                    vulnerability.setCwe(cwe);
                                } catch (NumberFormatException e) {
                                    // throw it away
                                }
                            }
                        }
                    }
                }

                // References
                final JsonObject ref0 = cve.getJsonObject("references");
                final JsonArray ref1 = ref0.getJsonArray("reference_data");
                final StringBuilder sb = new StringBuilder();
                for (int l = 0; l < ref1.size(); l++) {
                    final JsonObject ref2 = ref1.getJsonObject(l);
                    for (String s : ref2.keySet()) {
                        if ("url".equals(s)) {
                            // Convert reference to Markdown format
                            final String url = ref2.getString("url");
                            sb.append("* [").append(url).append("](").append(url).append(")\n");
                        }
                    }
                }
                final String references = sb.toString();
                if (references.length() > 0) {
                    vulnerability.setReferences(references.substring(0, references.lastIndexOf("\n")));
                }

                // Update the vulnerability
                LOGGER.debug("Synchronizing: " + vulnerability.getVulnId());
                qm.synchronizeVulnerability(vulnerability, false);
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing NVD JSON data");
            LOGGER.error(e.getMessage());
        }
        Event.dispatch(new IndexEvent(IndexEvent.Action.COMMIT, Vulnerability.class));
    }

    private void parseCveImpact(JsonObject cveItem, Vulnerability vuln) {
        final JsonObject imp0 = cveItem.getJsonObject("impact");
        final JsonObject imp1 = imp0.getJsonObject("baseMetricV2");
        if (imp1 != null) {
            final JsonObject imp2 = imp1.getJsonObject("cvssV2");
            if (imp2 != null) {
                final Cvss cvss = Cvss.fromVector(imp2.getJsonString("vectorString").getString());
                vuln.setCvssV2Vector(cvss.getVector()); // normalize the vector but use the scores from the feed
                vuln.setCvssV2BaseScore(imp2.getJsonNumber("baseScore").bigDecimalValue());
            }
            vuln.setCvssV2ExploitabilitySubScore(imp1.getJsonNumber("exploitabilityScore").bigDecimalValue());
            vuln.setCvssV2ImpactSubScore(imp1.getJsonNumber("impactScore").bigDecimalValue());
        }

        final JsonObject imp3 = imp0.getJsonObject("baseMetricV3");
        if (imp3 != null) {
            final JsonObject imp4 = imp3.getJsonObject("cvssV3");
            if (imp4 != null) {
                final Cvss cvss = Cvss.fromVector(imp4.getJsonString("vectorString").getString());
                vuln.setCvssV3Vector(cvss.getVector()); // normalize the vector but use the scores from the feed
                vuln.setCvssV3BaseScore(imp4.getJsonNumber("baseScore").bigDecimalValue());
            }
            vuln.setCvssV3ExploitabilitySubScore(imp3.getJsonNumber("exploitabilityScore").bigDecimalValue());
            vuln.setCvssV3ImpactSubScore(imp3.getJsonNumber("impactScore").bigDecimalValue());
        }
    }

}
