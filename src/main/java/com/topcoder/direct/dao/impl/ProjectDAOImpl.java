/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.impl;

import com.topcoder.direct.api.model.Project;
import com.topcoder.direct.api.model.ProjectBillingAccount;
import com.topcoder.direct.dao.ProjectDAO;
import com.topcoder.direct.dao.rowmapper.ProjectBillingAccountRowMapper;
import com.topcoder.direct.dao.rowmapper.ProjectRowMapper;
import com.topcoder.direct.util.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.topcoder.direct.util.DataAccess.readQuery;

/**
 * <p>
 * The projects DAO that will retrieve project data from database. This class is a JDBC implementation of
 * ProjectDAO interface.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
@Repository
public class ProjectDAOImpl implements ProjectDAO {

    /**
     * The JDBC template that for tcs_catalog database.
     */
    @Autowired
    @Qualifier("tcsCatalogJdbc")
    private NamedParameterJdbcTemplate tcsCatalogJdbcTemplate;

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
    @Override
    public List<Project> getMyProjects(List<String> customFilter, Map<String, Object> sqlParameters,
                                       String orderBy, boolean getAll) throws IOException {

        String queryName = getAll ? "get_all_projects" : "get_my_projects";

        String query = readQuery(queryName);

        query = addFilterToQuery(query, customFilter);

        if (orderBy != null) {
            query += " " + orderBy;
        }
        return DataAccess.executeSqlQuery(query, sqlParameters, tcsCatalogJdbcTemplate, new ProjectRowMapper());
    }

    /**
     * Get my project billings data from database.
     *
     * @param customFilter  - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return a list of project billing accounts instance.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public List<ProjectBillingAccount> getMyProjectsBillings(List<String> customFilter,
                                                             Map<String, Object> sqlParameters) throws IOException {
        String query = addFilterToQuery(readQuery("get_projects_billings"), customFilter);
        return DataAccess.executeSqlQuery(query, sqlParameters, tcsCatalogJdbcTemplate, new ProjectBillingAccountRowMapper());
    }

    /**
     * Get my projects count info from database.
     *
     * @param customFilter  - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @param getAll        - whether need to get all direct projects without checking permission.
     * @return the total count of my projects.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Integer getMyProjectsCount(List<String> customFilter,
                                      Map<String, Object> sqlParameters, boolean getAll) throws IOException {
        String queryName = getAll ? "get_all_projects_count" : "get_my_projects_count";
        return DataAccess.getSingleResultBySqlQuery(
                addFilterToQuery(readQuery(queryName), customFilter), sqlParameters, Integer.class,
                tcsCatalogJdbcTemplate).get(0);
    }

    /**
     * Add filter into query manually.
     *
     * @param query       The query content.
     * @param filterToAdd The filter that need to add into query.
     * @return the combined query
     */
    private String addFilterToQuery(String query, List<String> filterToAdd) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(query);

        // Add filters
        for (String f : filterToAdd) {
            stringBuilder.append(f);
        }

        return stringBuilder.toString();
    }
}
