/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import com.appirio.tech.core.api.v2.request.PostPutRequest;
import com.topcoder.direct.api.model.Project;
import com.topcoder.direct.rest.BaseDirectAPITest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.node.ObjectNode;
import org.hamcrest.CustomMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * This tests create new project rest API.
 * </p>
 *
 * @author GreatKevin
 * @version 1.0 (POC Assembly - Direct API Create direct project)
 */
public class CreateProjectTest extends BaseDirectAPITest {

    /**
     * The base url of this test.
     */
    private static final String BASE_URL = "/api/v2/projects";

    /**
     * Gets the Base URL for testing.
     *
     * @return the base URL.
     */
    @Override
    protected String getBaseURL() {
        return BASE_URL;
    }

    /**
     * Test jwt token is already expired.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void tokenIsExpired() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isBadRequest(), 400, EXPIRED_TOKEN, "JWT Expired.");
    }

    /**
     * Test user id in jwt token is 0.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void userIdInTokenIs0() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isBadRequest(), 400, ZERO_USER_ID_TOKEN, "The user id should be positive.");
    }

    /**
     * Test user id in jwt token is a string or something can't format.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void invalidUserIdToken() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isBadRequest(), 400, INVALID_USER_ID_TOKEN, "Invalid user id.");
    }

    /**
     * Test user id in jwt token is not existed.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void userIsNotExist() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isBadRequest(), 400, NON_EXIST_USER_ID_TOKEN, "The userId: 123 is not exist.");
    }

    /**
     * Test the invalid case when the project name is null.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectNameNull() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest(null, "description"),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project name should not be empty");
    }

    /**
     * Test the invalid case when the project name is empty.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectNameEmpty1() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest("", "description"),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project name should not be empty");
    }

    /**
     * Test the invalid case when the project name exceeds the max width.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectNameExceedMaxLength() throws Exception {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 201; ++i) {
            sb.append("a");
        }

        assertBadPostResponse("", constructProjectPostRequest(sb.toString(), "description"),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project name length should not exceed 200 chars");
    }

    /**
     * Test the invalid case when the project name is full of whitespaces only.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectNameEmpty2() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest("     ", "description"),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project name should not be empty");
    }

    /**
     * Test the invalid case when the project description is null.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectDescriptionNull() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest("name", null),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project description should not be empty");
    }

    /**
     * Test the invalid case when the project description is empty.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectDescriptionEmpty1() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest("name", ""),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project description should not be empty");
    }

    /**
     * Test the invalid case when the project description is full of whitespaces only.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectDescriptionEmpty2() throws Exception {
        assertBadPostResponse("", constructProjectPostRequest("name", "     "),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project description should not be empty");
    }

    /**
     * Test the invalid case when the project description exceeds the max width.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectDescriptionExceedMaxLength() throws Exception {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10001; ++i) {
            sb.append("a");
        }

        assertBadPostResponse("", constructProjectPostRequest("name", sb.toString()),
                status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, "The project description length should not exceed 10000 chars");
    }

    /**
     * Tests the invalid case when the params value contains unknown filed which does not belong to project.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void projectCreateWithUnknownFiled() throws Exception {
        PostPutRequest postPutRequest = constructValidProjectPostRequest();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("projectName", "name");
        objectNode.put("projectDescription", "description");
        objectNode.put("dummy", "dummy value");
        postPutRequest.setParam(objectNode);
        assertBadPostResponse("", postPutRequest,
                status().isInternalServerError(), 500, MEMBER_HEFFAN_TOKEN, "Fail to initialize [projects] resource from post request");
    }


    /**
     * Tests the success case of creating the project. The created project will be removed after the end of the testing.
     *
     * @throws Exception if any error occurs.
     */
    @Test
    public void createNewProjectSuccess() throws Exception {
        final String projectName = "new project name blabla";
        final String projectDescription = "project description long long long long long text of description";

        createPOSTRequest(getBaseURL(), MEMBER_HEFFAN_TOKEN, constructProjectPostRequest(projectName, projectDescription))
                .andExpect(status().isOk()).andExpect(jsonPath("$.result.content.id",
                new CustomMatcher<Integer>("Created new project") {
                    @Override
                    public boolean matches(Object item) {
                        String projectId = item.toString();

                        Map<String, Object> projectData = getProjectData(projectId);
                        Map<String, Object> projectPermissionData = getProjectPermissionData(projectId);

                        // compare project data
                        Assert.assertEquals("Project name does not match", projectName, projectData.get("name").toString());
                        Assert.assertEquals("Project description does not match", projectDescription,
                                projectData.get("description").toString());
                        Assert.assertEquals("Project status does not match", "1", projectData.get("project_status_id").toString());
                        Assert.assertEquals("Project create user does not match",
                                MEMBER_HEFFAN_USER_ID, projectData.get("user_id").toString());

                        // compare project permission data
                        Assert.assertEquals("Permission type name does not match", "3", projectPermissionData.get("permission_type_id").toString());
                        Assert.assertEquals("Permission resource id does not match", projectId, projectPermissionData.get("resource_id").toString());
                        Assert.assertEquals("Permission user id does not match", MEMBER_HEFFAN_USER_ID, projectPermissionData.get("user_id").toString());
                        Assert.assertEquals("isStudioFlag does not match", "0", projectPermissionData.get("is_studio").toString());

                        // delete the project and permission after checking
                        deleteProjectPermission(projectId);
                        deleteProject(projectId);

                        return true;
                    }
                }
        ));
    }


    /**
     * Test when there is no jwt token passed. An unauthorized error will be returned.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void unauthorized() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isUnauthorized(), 401, null, "Access Restricted.");
    }


    /**
     * Test when the user is not admin. An unauthorized error will be returned.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void unauthorized_NonAdmin() throws Exception {
        assertBadPostResponse("", constructValidProjectPostRequest(),
                status().isUnauthorized(), 401, MEMBER_SUPER_TOKEN, "Access Restricted.");
    }

    /**
     * Gets the project data with the specified projectId.
     *
     * @param projectId the id of the project.
     * @return the map stores the single row data.
     */
    private Map<String, Object> getProjectData(String projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", projectId);
        return tcsCatalogJdbcTemplate.queryForMap("SELECT * FROM tc_direct_project WHERE project_id = :projectId",
                params);
    }

    /**
     * Gets the project permission data with the specified projectId.
     *
     * @param projectId the id of the project.
     * @return the map stores the single row data.
     */
    private Map<String, Object> getProjectPermissionData(String projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", projectId);
        return tcsCatalogJdbcTemplate.queryForMap("SELECT * FROM user_permission_grant WHERE resource_id = :projectId",
                params);
    }

    /**
     * Deletes the project with the specified project id.
     *
     * @param projectId the project id.
     */
    private void deleteProject(String projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", projectId);
        tcsCatalogJdbcTemplate.update("DELETE FROM tc_direct_project WHERE project_id = :projectId",
                params);
    }

    /**
     * Deleted the project permission with the specified project id.
     *
     * @param projectId the project id.
     */
    private void deleteProjectPermission(String projectId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("projectId", projectId);
        tcsCatalogJdbcTemplate.update("DELETE FROM user_permission_grant WHERE resource_id = :projectId",
                params);
    }

    /**
     * Creates the post request object for the create project API.
     *
     * @param projectName the project name.
     * @param projectDescription the project description.
     * @return the created post request object
     * @throws Exception if any error occurs.
     */
    private static PostPutRequest constructProjectPostRequest(String projectName, String projectDescription)
            throws Exception {
        PostPutRequest request = new PostPutRequest();
        Project project = new Project();
        project.setProjectName(projectName);
        project.setProjectDescription(projectDescription);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.setFailOnUnknownId(false);
        ObjectWriter writer = objectMapper.filteredWriter(filterProvider);
        request.setParam(objectMapper.readTree(writer.writeValueAsString(project)));

        return request;
    }

    /**
     * Create a valid create project post request with valid project naem and description.
     *
     * @return the created project post request.
     * @throws Exception if any error occurs.
     */
    private static PostPutRequest constructValidProjectPostRequest() throws Exception {
        return constructProjectPostRequest("project name", "project description");
    }
}
