/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.impl;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.topcoder.direct.api.model.Project;
import com.topcoder.direct.api.model.ProjectBillingAccount;
import com.topcoder.direct.dao.ProjectDAO;
import com.topcoder.direct.dao.rowmapper.ProjectBillingAccountRowMapper;
import com.topcoder.direct.dao.rowmapper.ProjectRowMapper;
import com.topcoder.direct.util.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
 * <p>
 * Version 1.1 (POC Assembly - Direct API Create direct project)
 * <ul>
 *     <li>Added implementation methods of DaoBase<Project></li>
 * </ul>
 * </p>
 *
 * @author GreatKevin
 * @since 1.0 (TopCoder Direct API - Project Retrieval API)
 * @version 1.1 (POC Assembly - Direct API Create direct project)
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
     * The JDBC template that for corporate_oltp database.
     *
     * @since 1.1
     */
    @Autowired
    @Qualifier("corporateOltpJdbc")
    private NamedParameterJdbcTemplate corporateOltpJdbcTemplate;

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

    /**
     * Populate a list of project resource with the specified queryParameter (Not implemented)
     *
     * @param queryParameter the query parameter
     * @return the list of project resources
     * @throws Exception if any error occurs
     * @since 1.1
     */
    @Override
    public List<Project> populate(QueryParameter queryParameter) throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Populate a single project resource with the specified id (Not implemented)
     *
     * @param fieldSelector the field selector
     * @param cmcid the id of the project resource
     * @return the project resource
     * @throws Exception if any error occurs
     * @since 1.1
     */
    @Override
    public Project populateById(FieldSelector fieldSelector, CMCID cmcid) throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Inserts the provided project as the new project into persistence.
     *
     * @param project the project resource to insert.
     * @return the id of the newly created project
     * @throws Exception if any error occurs.
     * @since 1.1
     */
    @Transactional
    @Override
    public CMCID insert(Project project) throws Exception {

        Integer newProjectId = DataAccess.getNextSequenceValue("project_sequence", corporateOltpJdbcTemplate);
        project.setId(new CMCID(newProjectId));

        DataAccess.executeCreationQuery("create_new_project", new BeanPropertySqlParameterSource(project), corporateOltpJdbcTemplate);

        Integer newPermissionId = DataAccess.getNextSequenceValue("permission_seq", corporateOltpJdbcTemplate);
        MapSqlParameterSource permissionParams = new MapSqlParameterSource();
        permissionParams.addValue("userPermissionGrantId", newPermissionId);
        permissionParams.addValue("userId", project.getProjectCreatedBy());
        permissionParams.addValue("resourceId", project.getId().getId());

        DataAccess.executeCreationQuery("user_project_full_permission_grant", permissionParams, corporateOltpJdbcTemplate);

        return project.getId();
    }

    /**
     * Update with the specified project (Not Implemented)
     *
     * @param project the project resource to update.
     * @return the id of the updated project
     * @throws Exception if any error occurs
     * @since 1.1
     */
    @Override
    public CMCID update(Project project) throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Delete the project of the specified ID (Not Implemented)
     *
     * @param cmcid the id of the project
     * @throws Exception if any error
     * @since 1.1
     */
    @Override
    public void delete(CMCID cmcid) throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Gets the class of the project resource this DAO handles.
     *
     * @return the class object of project resource.
     * @since 1.1
     */
    @Override
    public Class<Project> getHandlingClass() {
        return Project.class;
    }
}
