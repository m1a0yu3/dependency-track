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
package org.owasp.dependencytrack;

import alpine.Config;

/**
 * Alpine {@link Config.Key} implementation to support Dependency-Track specific
 * configuration keys.
 *
 * @author Steve Springett
 * @since 3.0.0
 */
public enum DependencyTrackConfigKey implements Config.Key {

    DATASOURCE_VULN_DB_ENABLED      ("datasource.vulndb.enabled", false),
    DATASOURCE_VULN_DB_KEY          ("datasource.vulndb.key", null),
    DATASOURCE_VULN_DB_SECRET       ("datasource.vulndb.secret", null),
    DC_DB_DRIVERNAME                ("dcheck.database.drivername",null),
    DC_DB_CONNECTION_STRING         ("dcheck.database.connectionstring",null),
    DC_DB_DRIVERPATH                ("dcheck.database.driverpath",null),
    DC_DB_USER                      ("dcheck.database.user",null),
    DC_DB_PASSWORD                  ("dcheck.database.password",null);

    private String propertyName;
    private Object defaultValue;
    DependencyTrackConfigKey(String item, Object defaultValue) {
        this.propertyName = item;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

}
