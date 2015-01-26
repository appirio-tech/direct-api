/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.metadata.CountableMetadata;
import com.appirio.tech.core.api.v2.metadata.Metadata;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.FilterParameter;
import com.appirio.tech.core.api.v2.request.LimitQuery;
import com.appirio.tech.core.api.v2.request.OrderByQuery;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.request.SortOrder;
import com.appirio.tech.core.api.v2.service.AbstractMetadataService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;
import com.topcoder.direct.api.model.Project;
import com.topcoder.direct.api.model.ProjectBillingAccount;
import com.topcoder.direct.api.security.AccessLevel;
import com.topcoder.direct.api.security.DirectAuthenticationToken;
import com.topcoder.direct.api.security.SecurityUtil;
import com.topcoder.direct.dao.ProjectDAO;
import com.topcoder.direct.exception.BadRequestException;
import com.topcoder.direct.exception.ServerInternalException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topcoder.direct.util.Helper.isNull;
import static com.topcoder.direct.util.ServiceHelper.parseFilterValueToValueList;
import static com.topcoder.direct.util.ServiceHelper.populateLimitQuery;

/**
 * This is the service implementation for the My Projects API.
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
@Service
public class ProjectService extends AbstractMetadataService implements RESTQueryService<Project> {

    /**
     * Logger instance.
     */
    private static final Logger LOG = Logger.getLogger(ProjectService.class);


    /**
     * project name exact match filter
     */
    private static final String PROJECT_NAME_EXACT_MATCH_FILTER = " AND tdp.name IN (:project_names)\n";

    /**
     * project id filter.
     */
    private static final String PROJECT_ID_FILTER = " AND tdp.project_id IN (:project_ids)\n";

    /**
     * Billing account name exact match filter.
     */
    private static final String BILLING_NAME_EXACT_MATCH_FILTER = " AND EXISTS (SELECT dpa.direct_project_account_id\n" +
            " FROM direct_project_account dpa, tt_project b\n" +
            " WHERE dpa.billing_account_id = b.project_id AND dpa.project_id = tdp.project_id AND b.name IN (:billing_names) )\n";

    /**
     * Billing Id filter.
     */
    private static final String BILLING_ID_FILTER = " AND EXISTS (select dpa.direct_project_account_id FROM direct_project_account dpa \n" +
            "WHERE dpa.project_id = tdp.project_id AND dpa.billing_account_id IN (:billing_ids))\n";

    /**
     * Field mapping for ordering.
     */
    private static final Map<String, String> ORDER_BY_FIELDS = new HashMap<String, String>() {
        {
            put("projectId".toLowerCase(), "project_id");
            put("projectName".toLowerCase(), "project_name");
            put("projectStatus".toLowerCase(), "project_status_name");
            put("projectCreatedDate".toLowerCase(), "project_created_date");
            put("projectUpdatedDate".toLowerCase(), "project_updated_date");
        }
    };

    /**
     * The default sorting order to use when there is no sorting.
     */
    private static final String DEFAULT_SORT_FIELD = "projectId";

    /**
     * Allowed scope values for my projects api when user is admin.
     */
    private static final List<String> ALLOWED_SCOPE = Arrays.asList("all", "my");

    /**
     * The project DAO.
     */
    @Autowired
    private ProjectDAO projectDAO;


    /**
     * Gets the project by single project ID (not supported now)
     *
     * @param fieldSelector the field selector
     * @param cmcid         the id
     * @return the project
     * @throws Exception if any error occurred
     */
    @Override
    public Project handleGet(FieldSelector fieldSelector, CMCID cmcid) throws Exception {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Retrieves projects using the provided specification.
     *
     * @param httpServletRequest the servlet request
     * @param query              the filter and output specification
     * @return the challenge data requested, may be empty, never null
     * @throws BadRequestException     for any validation failure in the request data
     * @throws UnauthorizedException   if the request principal is not logged in as a member or admin roles
     * @throws ServerInternalException for IOExceptions caught from the DAO
     *                                 Note: all RuntimeExceptions are propagated to the framework exception handlers.
     */
    @Override
    public List<Project> handleGet(HttpServletRequest httpServletRequest, QueryParameter query) {
        DirectAuthenticationToken identity = SecurityUtil.getAuthentication(httpServletRequest);
        identity.authorize(AccessLevel.ADMIN, AccessLevel.MEMBER);

        List<Project> projects;
        List<String> customFilter;

        try {
            Map<String, Object> sqlParameters = new HashMap<String, Object>();

            // validate filters
            validateQuery(query, identity.getAccessLevel() == AccessLevel.ADMIN);

            // for access checking
            sqlParameters.put("user_id", identity.getUserId());

            customFilter = getProjectFilters(query, sqlParameters);

            // limit results
            populateLimitQuery(query, sqlParameters);
            String orderClause = getOrderClause(query.getOrderByQuery());

            List<String> scope = parseFilterValueToValueList(query.getFilter().get("scope"), true, LOG);

            boolean needsToGetAllProjects = identity.getAccessLevel() == AccessLevel.ADMIN &&
                    (scope.size() > 0 && scope.get(0).equalsIgnoreCase("all"));


            projects = projectDAO.getMyProjects(customFilter, sqlParameters, orderClause, needsToGetAllProjects);

            if (projects.size() > 0) {
                mergeBillingsToProject(projects);
            }
        } catch (IOException e) {
            throw new ServerInternalException("An error occurred while querying for direct projects", e);
        }

        return projects;
    }

    /**
     * Validate input parameters for my projects api.
     *
     * @param query   the query.
     * @param isAdmin whether the user is admin (used for validate scope filter)
     * @throws BadRequestException if the any of the query parameters are invalid
     * @throws IOException         When an error occurs while getting user handle of logged in user
     */
    private void validateQuery(QueryParameter query, boolean isAdmin) throws IOException {
        FilterParameter filter = query.getFilter();

        if (filter.contains("id")) {
            List<String> values = parseFilterValueToValueList(filter.get("id"), false, LOG);
            for (String val : values) {
                try {
                    int id = Integer.parseInt(val);
                    if (id <= 0) {
                        throw new BadRequestException("Project Id should be positive.");
                    }
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException("Invalid direct project ID.");
                }
            }
        }

        if (filter.contains("billingId")) {
            List<String> values = parseFilterValueToValueList(filter.get("billingId"), false, LOG);
            for (String val : values) {
                try {
                    int id = Integer.parseInt(val);
                    if (id <= 0) {
                        throw new BadRequestException("Billing Id should be positive.");
                    }
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException("Invalid Billing ID.");
                }
            }
        }

        if (isAdmin && filter.contains("scope")) {
            List<String> values = parseFilterValueToValueList(filter.get("scope"), true, LOG);
            if (!values.isEmpty()) {
                // only the first one is considered
                String scopeValue = values.get(0);
                if (!ALLOWED_SCOPE.contains(scopeValue)) {
                    throw new BadRequestException("Invalid scope, should be one value of: " + ALLOWED_SCOPE);
                }
            }
        }

        // check limits, technically this should be done by the framework since it built the query object
        // but for compatibility with the existing code, we perform some checks for now
        LimitQuery limitQuery = query.getLimitQuery();
        if (limitQuery != null) {
            if (limitQuery.getLimit() != null) {
                if (limitQuery.getLimit() == 0 || limitQuery.getLimit() < -1) {
                    throw new BadRequestException("Invalid limit, -1 if you want to get all records.");
                }
            }
            if (limitQuery.getOffset() != null) {
                if (limitQuery.getOffset() < 0) {
                    throw new BadRequestException("Invalid offset, must be 0 or more.");
                }
            }
        }
    }

    /**
     * Gets the project filters to be used in the query and set the appropriate sql parameters as well
     *
     * @param query         The filters and limits to be used in the query.
     * @param sqlParameters The sql parameters object that will be used when execute query.
     * @return The list of filter content that need to add into query manually.
     * @throws IOException If something went wrong when read the query.
     */
    private List<String> getProjectFilters(QueryParameter query,
                                           Map<String, Object> sqlParameters) throws IOException {


        FilterParameter filter = query.getFilter();
        List<String> filterToAdd = new ArrayList<String>();

        boolean hasProjectNameExactMatchFilter = false;
        boolean hasBillingNameExactMatchFilter = false;

        if (filter.contains("name")) {
            // Get the project names to do exact match
            List<String> projectNames = parseFilterValueToValueList(filter.get("name"), false, LOG);

            if (!projectNames.isEmpty()) {
                sqlParameters.put("project_names", projectNames);
                filterToAdd.add(PROJECT_NAME_EXACT_MATCH_FILTER);
                hasProjectNameExactMatchFilter = true;
            }
        }


        if (filter.contains("nameLike") && !hasProjectNameExactMatchFilter) {
            // Get the project name patterns to do partial match
            List<String> projectNamePatterns = parseFilterValueToValueList(filter.get("nameLike"), false, LOG);
            if (!projectNamePatterns.isEmpty()) {
                List<String> nameLikeFilters = new ArrayList<String>();
                int index = 0;

                for (String filterValue : projectNamePatterns) {
                    sqlParameters.put("project_name_pattern" + index, filterValue);
                    nameLikeFilters.add(" tdp.name MATCHES :project_name_pattern" + index);
                    index++;
                }
                filterToAdd.add("AND (" + StringUtils.join(nameLikeFilters, " OR ") + ") \n");
            }
        }


        if (filter.contains("id")) {
            List<String> values = parseFilterValueToValueList(filter.get("id"), true, LOG);
            List<Integer> projectIds = new ArrayList<Integer>();
            for (String id : values) {
                projectIds.add(Integer.valueOf(id));
            }
            sqlParameters.put("project_ids", projectIds);
            filterToAdd.add(PROJECT_ID_FILTER);
        }

        if (filter.contains("billingName")) {
            // Get the billing account names to do exact match
            List<String> billingNames = parseFilterValueToValueList(filter.get("billingName"), false, LOG);

            if (!billingNames.isEmpty()) {
                sqlParameters.put("billing_names", billingNames);
                filterToAdd.add(BILLING_NAME_EXACT_MATCH_FILTER);
                hasBillingNameExactMatchFilter = true;
            }
        }

        if (filter.contains("billingNameLike") && !hasBillingNameExactMatchFilter) {
            // Get the billing account name patterns to do partial match
            List<String> billingNamePatterns = parseFilterValueToValueList(filter.get("billingNameLike"), false, LOG);
            if (!billingNamePatterns.isEmpty()) {
                List<String> billingNameLikeFilters = new ArrayList<String>();
                int index = 0;

                for (String filterValue : billingNamePatterns) {
                    sqlParameters.put("billing_name_pattern" + index, filterValue);
                    billingNameLikeFilters.add(" b.name MATCHES :billing_name_pattern" + index);
                    index++;
                }

                filterToAdd.add(" AND EXISTS (SELECT dpa.direct_project_account_id\n" +
                        " FROM direct_project_account dpa, tt_project b\n" +
                        " WHERE dpa.billing_account_id = b.project_id AND dpa.project_id = tdp.project_id AND " +
                        "(" + StringUtils.join(billingNameLikeFilters, " OR ") + ")) \n");
            }
        }

        if (filter.contains("billingId")) {
            List<String> values = parseFilterValueToValueList(filter.get("billingId"), true, LOG);
            List<Integer> billingIds = new ArrayList<Integer>();
            for (String id : values) {
                billingIds.add(Integer.valueOf(id));
            }
            sqlParameters.put("billing_ids", billingIds);
            filterToAdd.add(BILLING_ID_FILTER);
        }

        if (filter.contains("projectStatus")) {
            List<String> status = parseFilterValueToValueList(filter.get("projectStatus"), true, LOG);
            if (!status.isEmpty()) {
                List<String> psFilters = new ArrayList<String>();
                int index = 0;
                for (String filterValue : status) {
                    boolean isNumber = true;
                    Integer projectStatusId = 0;
                    try {
                        projectStatusId = Integer.valueOf(filterValue.toString());
                    } catch (NumberFormatException nef) {
                        // Do nothing.
                        isNumber = false;
                    }
                    if (isNumber) {
                        sqlParameters.put("project_status_id" + index, projectStatusId);
                        psFilters.add(" tdp.project_status_id = :project_status_id" + index);
                    } else {
                        sqlParameters.put("project_status_name" + index, filterValue);
                        psFilters.add(" LOWER(ps.name) = :project_status_name" + index);
                    }
                    index++;
                }
                filterToAdd.add("AND (" + StringUtils.join(psFilters, " OR ") + ") \n");
            }
        }

        return filterToAdd;
    }

    /**
     * Generates the order by clause for projects.
     *
     * @param query the order query requested
     * @return the order by clause
     */
    private String getOrderClause(OrderByQuery query) {
        StringBuilder order = new StringBuilder();
        String fieldName = query.getOrderByField();
        boolean useDefaultSortingOrder = false;
        if (fieldName == null || fieldName.trim().length() == 0) {
            fieldName = DEFAULT_SORT_FIELD;
            useDefaultSortingOrder = true;
        }

        fieldName = fieldName.toLowerCase();
        if (ORDER_BY_FIELDS.get(fieldName) == null) {
            throw new BadRequestException("Sorting is not supported for requested field:" + fieldName);
        }

        order.append(" ORDER BY " + ORDER_BY_FIELDS.get(fieldName)).append(" ");

        if (!useDefaultSortingOrder && query.getSortOrder() != null) { // specified direction
            if (query.getSortOrder() == SortOrder.ASC_NULLS_FIRST) {
                order.append("ASC ");
            } else if (query.getSortOrder() == SortOrder.DESC_NULLS_LAST) {
                order.append("DESC ");
            } else {
                throw new BadRequestException("Specified sort order is not supported. " + query.getSortOrder());
            }
        }

        if (useDefaultSortingOrder) {
            order.append("DESC ");
        }

        // add a secondary sorting for non unique sort requests
        if (!"projectId".equalsIgnoreCase(fieldName)) {
            order.append(", project_id DESC");
        }

        order.append("\n");
        return order.toString();
    }

    /**
     * Merges the corresponding billing accounts to projects.
     *
     * @param projects The projects to be bound with billings.
     * @throws IOException When an error occurs while querying the billings.
     */
    private void mergeBillingsToProject(List<Project> projects) throws IOException {

        // NOTE: get all the projects from the direct projects
        List<Integer> projectIds = new ArrayList<Integer>();
        for (Project project : projects) {
            projectIds.add(Integer.valueOf(project.getId().toString()));
        }
        Map<String, Object> sqlParameters = new HashMap<String, Object>();
        sqlParameters.put("project_ids", projectIds);

        List<ProjectBillingAccount> billings = projectDAO.getMyProjectsBillings(new ArrayList<String>(), sqlParameters);

        Map<Integer, List<ProjectBillingAccount>> project2BillingsMap = new HashMap<Integer, List<ProjectBillingAccount>>();

        for (ProjectBillingAccount billing : billings) {
            Integer projectId = billing.getProjectId();
            if (isNull(project2BillingsMap.get(projectId))) {
                // We don't have billings for this project yet
                List<ProjectBillingAccount> list = new ArrayList<ProjectBillingAccount>();
                list.add(billing);
                project2BillingsMap.put(projectId, list);
            } else {
                // Otherwise
                project2BillingsMap.get(projectId).add(billing);
            }
        }

        for (Project project : projects) {
            List<ProjectBillingAccount> projectBillings = project2BillingsMap.get(Integer.valueOf(project.getId().toString()));

            if (projectBillings == null) {
                projectBillings = new ArrayList<ProjectBillingAccount>();
            }
            project.setBillings(projectBillings);
        }
    }

    /**
     * Gets the metadata
     *
     * @param request the http servlet request.
     * @param query   the QueryParameter instance.
     * @return the metadata
     * @throws Exception if any error.
     */
    @Override
    public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
        CountableMetadata metadata = new CountableMetadata();

        DirectAuthenticationToken identity = SecurityUtil.getAuthentication(request);
        identity.authorize(AccessLevel.ADMIN, AccessLevel.MEMBER);

        try {
            Map<String, Object> sqlParameters = new HashMap<String, Object>();
            List<String> customFilter;

            // validate filters
            validateQuery(query, identity.getAccessLevel() == AccessLevel.ADMIN);

            // for access checking
            sqlParameters.put("user_id", identity.getUserId());

            customFilter = getProjectFilters(query, sqlParameters);

            // limit results
            populateLimitQuery(query, sqlParameters);

            List<String> scope = parseFilterValueToValueList(query.getFilter().get("scope"), true, LOG);

            boolean needsToGetAllProjects = identity.getAccessLevel() == AccessLevel.ADMIN &&
                    (scope.size() > 0 && scope.get(0).equalsIgnoreCase("all"));


            Integer projectsCount = projectDAO.getMyProjectsCount(customFilter, sqlParameters, needsToGetAllProjects);

            if (projectsCount != null) {
                metadata.setTotalCount(projectsCount);
            }
        } catch (IOException e) {
            throw new ServerInternalException("An error occurred while querying for projects total count", e);
        }

        return metadata;
    }

    /**
     * Returns the resource path for project.
     *
     * @return Project.RESOURCE_PATH
     */
    @Override
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return Project.RESOURCE_PATH;
    }
}
