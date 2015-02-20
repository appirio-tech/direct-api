/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao;

import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.topcoder.direct.api.model.Project;
import com.topcoder.direct.api.model.ProjectBillingAccount;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Defines the interface of the projects DAO that will retrieve project data from database.
 * </p>
 *
 * <p>
 * Version 1.1 (POC Assembly - Direct API Create direct project)
 * <ul>
 *     <li>Change the interface to extends DaoBase<Project></li>
 * </ul>
 * </p>
 *
 * @author GreatKevin
 * @since 1.0 (TopCoder Direct API - Project Retrieval API)
 * @version 1.1 (POC Assembly - Direct API Create direct project)
 */
public interface ProjectDAO extends DaoBase<Project> {
    /**
     * Get my projects from database.
     *
     * @param customFilter  - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @param orderBy       - the order clause
     * @param getAll        - whether need to get all direct projects without checking permission
     * @return A list of projects.
     * @throws IOException if error occurred while read query.
     */
    List<Project> getMyProjects(List<String> customFilter, Map<String, Object> sqlParameters, String orderBy, boolean getAll)
            throws IOException;

    /**
     * Get my project billings data from database.
     *
     * @param customFilter  - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return a list of project billing accounts instance.
     * @throws IOException if error occurred while read query.
     */
    List<ProjectBillingAccount> getMyProjectsBillings(List<String> customFilter, Map<String, Object> sqlParameters) throws IOException;

    /**
     * Get my projects count info from database.
     *
     * @param customFilter  - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @param getAll        - whether need to get all direct projects without checking permission.
     * @return the total count of my projects.
     * @throws IOException if error occurred while read query.
     */
    Integer getMyProjectsCount(List<String> customFilter, Map<String, Object> sqlParameters, boolean getAll) throws IOException;

}
