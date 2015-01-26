/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.model;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Represents the direct project model.
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
@Component
public class Project extends AbstractIdResource {

    /**
     * Query API resource mapping.
     */
    public static final String RESOURCE_PATH = "projects";

    /**
     * Empty constructor.
     */
    public Project() {

    }

    /**
     * The project name.
     */
    private String projectName;

    /**
     * The project status id.
     */
    private Integer projectStatusId;

    /**
     * The project status name.
     */
    private String projectStatusName;

    /**
     * The project created date.
     */
    private String projectCreatedDate;

    /**
     * The project created by user id.
     */
    private Integer projectCreatedBy;

    /**
     * The project latest updated date.
     */
    private String projectLastUpdatedDate;

    /**
     * The billing accounts associated with the project.
     */
    private List<ProjectBillingAccount> billings;

    /**
     * Hide the account ID from displaying in API json response, it's useless.
     *
     * @return the account id.
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public CMCID getAccountId() {
        return null;
    }

    /**
     * Gets the resource path of the model
     *
     * @return the resource path.
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * Gets the project name.
     *
     * @return the project name.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the project name.
     *
     * @param projectName the project name.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the project status id.
     *
     * @return the project status id.
     */
    public Integer getProjectStatusId() {
        return projectStatusId;
    }

    /**
     * Sets the project status id.
     *
     * @param projectStatusId the project status id.
     */
    public void setProjectStatusId(Integer projectStatusId) {
        this.projectStatusId = projectStatusId;
    }

    /**
     * Gets the projects status name.
     *
     * @return the projects status name.
     */
    public String getProjectStatusName() {
        return projectStatusName;
    }

    /**
     * Sets the project status name.
     *
     * @param projectStatusName the project status name.
     */
    public void setProjectStatusName(String projectStatusName) {
        this.projectStatusName = projectStatusName;
    }

    /**
     * Gets the project created date.
     *
     * @return the project created date.
     */
    public String getProjectCreatedDate() {
        return projectCreatedDate;
    }

    /**
     * Sets the project created date.
     *
     * @param projectCreatedDate the project created date.
     */
    public void setProjectCreatedDate(String projectCreatedDate) {
        this.projectCreatedDate = projectCreatedDate;
    }

    /**
     * Gets the project created by user id.
     *
     * @return the project created by user id.
     */
    public Integer getProjectCreatedBy() {
        return projectCreatedBy;
    }

    /**
     * Sets the project created by user id.
     *
     * @param projectCreatedBy the project created by user id.
     */
    public void setProjectCreatedBy(Integer projectCreatedBy) {
        this.projectCreatedBy = projectCreatedBy;
    }

    /**
     * Gets the project last updated date.
     *
     * @return the project last updated date.
     */
    public String getProjectLastUpdatedDate() {
        return projectLastUpdatedDate;
    }

    /**
     * Sets the project last updated date.
     *
     * @param projectLastUpdatedDate the project last updated date.
     */
    public void setProjectLastUpdatedDate(String projectLastUpdatedDate) {
        this.projectLastUpdatedDate = projectLastUpdatedDate;
    }

    /**
     * Gets the project billings.
     *
     * @return the project billings.
     */
    public List<ProjectBillingAccount> getBillings() {
        return billings;
    }

    /**
     * Sets the project billings.
     *
     * @param billings the project billings.
     */
    public void setBillings(List<ProjectBillingAccount> billings) {
        this.billings = billings;
    }
}
