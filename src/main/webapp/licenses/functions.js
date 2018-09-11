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

"use strict";

/**
 * Called by bootstrap table to format the data in the licenses table.
 */
function formatLicensesTable(res) {
    for (let i=0; i<res.length; i++) {

        if (res[i].isOsiApproved === true) {
            res[i].osiApprovedLabel = '<i class="fa fa-check-square-o" aria-hidden="true"></i>';
        } else {
            res[i].osiApprovedLabel = '';
        }

        let licenseurl = "../license/?licenseId=" + res[i].licenseId;
        res[i].licensehref = "<a href=\"" + licenseurl + "\">" + res[i].licenseId + "</a>";
    }
    return res;
}

function updateStats(metric) {
    $("#statTotalProjects").html(metric.projects);
    $("#statVulnerableProjects").html(metric.vulnerableProjects);
    $("#statTotalComponents").html(metric.components);
    $("#statVulnerableComponents").html(metric.vulnerableComponents);
    $("#statPortfolioVulnerabilities").html(metric.vulnerabilities);
    $("#statLastMeasurement").html(filterXSS($common.formatTimestamp(metric.lastOccurrence, true)));
    $("#statInheritedRiskScore").html(metric.inheritedRiskScore);
}

/**
 * Setup events and trigger other stuff when the page is loaded and ready.
 */
$(document).ready(function () {

    $rest.getPortfolioCurrentMetrics(function(metrics) {
        updateStats(metrics);
    });

});