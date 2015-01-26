/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.model;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

/**
 * Represents the billing account model.
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
@Component
public class ProjectBillingAccount extends AbstractIdResource {
    /**
     * API Query resource path.
     */
    public static final String RESOURCE_PATH = "billings";

    /**
     * The direct project id the billing account is bound to.
     */
    private Integer projectId;

    /**
     * The name.
     */
    private String name;

    /**
     * empty constructor.
     */
    public ProjectBillingAccount() {
    }

    /**
     * Gets the account id.
     *
     * @return the account id.
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public CMCID getAccountId() {
        return null;
    }

    /**
     * Gets the resource path.
     *
     * @return the resource path.
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * Gets the project id.
     *
     * @return the project id.
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * Sets the project id.
     *
     * @param projectId the project id.
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
