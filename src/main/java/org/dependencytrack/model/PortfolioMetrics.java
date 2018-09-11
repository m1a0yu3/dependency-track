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
package org.dependencytrack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Metrics for the entire application as a whole, not specific to individual
 * components or projects.
 *
 * @author Steve Springett
 * @since 3.0.0
 */
@PersistenceCapable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioMetrics implements Serializable {

    private static final long serialVersionUID = -7690624184866776922L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
    @JsonIgnore
    private long id;

    @Persistent
    @Column(name = "CRITICAL")
    private int critical;

    @Persistent
    @Column(name = "HIGH")
    private int high;

    @Persistent
    @Column(name = "MEDIUM")
    private int medium;

    @Persistent
    @Column(name = "LOW")
    private int low;

    @Persistent
    @Column(name = "VULNERABILITIES")
    private int vulnerabilities;

    @Persistent
    @Column(name = "PROJECTS")
    private int projects;

    @Persistent
    @Column(name = "VULNERABLEPROJECTS")
    private int vulnerableProjects;

    @Persistent
    @Column(name = "COMPONENTS")
    private int components;

    @Persistent
    @Column(name = "VULNERABLECOMPONENTS")
    private int vulnerableComponents;

    @Persistent
    @Column(name = "DEPENDENCIES", allowsNull = "true") // New column, must allow nulls on existing databases
    private Integer dependencies;

    @Persistent
    @Column(name = "VULNERABLEDEPENDENCIES", allowsNull = "true") // New column, must allow nulls on existing databases
    private Integer vulnerableDependencies;

    @Persistent
    @Column(name = "SUPPRESSED")
    private int suppressed;

    @Persistent
    @Column(name = "RISKSCORE")
    private double inheritedRiskScore;

    @Persistent
    @Column(name = "FIRST_OCCURRENCE", allowsNull = "false")
    @NotNull
    @Index(name = "PORTFOLIOMETRICS_FIRST_OCCURRENCE_IDX")
    private Date firstOccurrence;

    @Persistent
    @Column(name = "LAST_OCCURRENCE", allowsNull = "false")
    @NotNull
    @Index(name = "PORTFOLIOMETRICS_LAST_OCCURRENCE_IDX")
    private Date lastOccurrence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(int vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public int getProjects() {
        return projects;
    }

    public void setProjects(int projects) {
        this.projects = projects;
    }

    public int getVulnerableProjects() {
        return vulnerableProjects;
    }

    public void setVulnerableProjects(int vulnerableProjects) {
        this.vulnerableProjects = vulnerableProjects;
    }

    public int getComponents() {
        return components;
    }

    public void setComponents(int components) {
        this.components = components;
    }

    public int getVulnerableComponents() {
        return vulnerableComponents;
    }

    public void setVulnerableComponents(int vulnerableComponents) {
        this.vulnerableComponents = vulnerableComponents;
    }

    public int getDependencies() {
        return dependencies;
    }

    public void setDependencies(int dependencies) {
        this.dependencies = dependencies;
    }

    public int getVulnerableDependencies() {
        return vulnerableDependencies;
    }

    public void setVulnerableDependencies(int vulnerableDependencies) {
        this.vulnerableDependencies = vulnerableDependencies;
    }

    public int getSuppressed() {
        return suppressed;
    }

    public void setSuppressed(int suppressed) {
        this.suppressed = suppressed;
    }

    public double getInheritedRiskScore() {
        return inheritedRiskScore;
    }

    public void setInheritedRiskScore(double inheritedRiskScore) {
        this.inheritedRiskScore = inheritedRiskScore;
    }

    public Date getFirstOccurrence() {
        return firstOccurrence;
    }

    public void setFirstOccurrence(Date firstOccurrence) {
        this.firstOccurrence = firstOccurrence;
    }

    public Date getLastOccurrence() {
        return lastOccurrence;
    }

    public void setLastOccurrence(Date lastOccurrence) {
        this.lastOccurrence = lastOccurrence;
    }
}
