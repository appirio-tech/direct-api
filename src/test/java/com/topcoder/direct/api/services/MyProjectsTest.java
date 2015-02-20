/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import com.topcoder.direct.rest.BaseDirectAPITest;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * This tests my projects rest API.
 * </p>
 *
 * <p>
 * Version 1.1 (POC Assembly - Direct API Create direct project)
 * <ul>
 *     <li>Removed unused test case on support method post</li>
 * </ul>
 * </p>
 *
 * @author GreatKevin
 * @version 1.1
 */
public class MyProjectsTest extends BaseDirectAPITest {


    /**
     * The base url of this test.
     */
    private static final String BASE_URL = "/api/v2/projects";

    /**
     * The number of tests have been run.
     *
     * @since 1.2
     */
    private static int currentRunningCount = 0;

    /**
     * The total tests count of MyChallengesTest
     *
     */
    private static int testsCount = -1;

    /**
     * Counts the number of tests in this junit test class.
     *
     * @return the number of tests.
     *
     */
    private int countTests() {
        if (testsCount < 0) {
            int count = 0;
            for (Method m : this.getClass().getMethods()) {
                if (m.isAnnotationPresent(Test.class)) {
                    count++;
                }
            }
            testsCount = count;
        }

        return testsCount;
    }

    /**
     * The empty map of parameters used by jdbc template when updating.
     */
    private static final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

    /**
     * This method will insert test data into topcoder database.
     *
     * @throws java.io.IOException if any error occurred during reading file.
     */
    public void insertTestData() throws IOException {
        loadDataFromFile("test/data/my_projects/setup_test_data.sql", tcsCatalogJdbcTemplate);
    }

    /**
     * Setup the env.
     *
     * @throws IOException if any error occurred during reading file.
     */
    @Before
    public void setup() throws IOException {
        if(currentRunningCount == 0) {
            cleanTestData();
            insertTestData();
        }
        currentRunningCount++;
    }


    /**
     * Gets the base API endpoint URL used for testing.
     *
     * @return the url
     */
    @Override
    protected String getBaseURL() {
        return BASE_URL;
    }

    /**
     * This method will delete all the test data insert into topcoder database in this test file.
     *
     * @throws IOException if any error occurred during reading file.
     */
    public void cleanTestData() throws IOException {
        tcsCatalogJdbcTemplate
                .update(readFile("test/data/my_projects/clean_up_test_data.sql"), EMPTY_MAP);
    }

    /**
     * Tear down the env.
     *
     * @throws IOException if any error occurred during reading file.
     */
    @After
    public void tearDown() throws IOException {
        if(currentRunningCount == countTests()) {
            cleanTestData();
        }
    }

    /**
     * Test limit is negative.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limitIsNegative() throws Exception {
        assertBadResponse("?limit=-2", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test limit is zero.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limitIsZero() throws Exception {
        assertBadResponse("?limit=0", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test limit is positive.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limitIsPositive() throws Exception {
        assertBadResponse("?limit=1", status().isOk(), 200, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test limit is string.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limitIsString() throws Exception {
        assertBadResponse("?limit=abc", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test limit is double.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limitIsDouble() throws Exception {
        assertBadResponse("?limit=2.2", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test offset is negative.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void offsetIsNegative() throws Exception {
        assertBadResponse("?limit=1&offset=-2", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test offset is zero.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void offsetIsZero() throws Exception {
        assertBadResponse("?limit=1&offset=0", status().isOk(), 200, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test offset is double.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void offsetIsDouble() throws Exception {
        assertBadResponse("?limit=1&offset=3.3", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test offset is string.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void offsetIsString() throws Exception {
        assertBadResponse("?limit=1&offset=abc", status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test offset is positive.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void offsetIsPositive() throws Exception {
        assertBadResponse("?limit=1&offset=1", status().isOk(), 200, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test direct project id filter a negative integer.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsNegative() throws Exception {
        assertBadResponse("?filter=" + encode("id=-2"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
                null);
    }

    /**
     * Test direct project id filter is 0.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsZero() throws Exception {
        assertBadResponse("?filter=" + encode("id=0"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
                null);
    }

    /**
     * Test direct project id filter is a double value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsDouble() throws Exception {
        assertBadResponse("?filter=" + encode("id=2.234"), status().isBadRequest(), 400,
                MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test direct project id filter is a string value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsString() throws Exception {
        assertBadResponse("?filter=" + encode("id=abc"), status().isBadRequest(), 400,
                MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test billing account id filter a negative integer.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void billingAccountIdIsNegative() throws Exception {
        assertBadResponse("?filter=" + encode("billingId=-2"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
                null);
    }

    /**
     * Test billing account id filter is 0.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void billingAccountIdIsZero() throws Exception {
        assertBadResponse("?filter=" + encode("billingId=0"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
                null);
    }

    /**
     * Test billing account id filter is a double value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void billingAccountIdIsDouble() throws Exception {
        assertBadResponse("?filter=" + encode("billingId=2.234"), status().isBadRequest(), 400,
                MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test billing account id filter is a string value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void billingAccountIdIsString() throws Exception {
        assertBadResponse("?filter=" + encode("billingId=abc"), status().isBadRequest(), 400,
                MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test scope is an invalid value when the request user is admin
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void typeIsAdminScopeInvalid() throws Exception {
        assertBadResponse("?filter=" + encode("scope=badScope"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test scopes are invalid when the request user is admin
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void typeIsAdminScopeInvalid_2() throws Exception {
        assertBadResponse("?filter=" + encode("scope=in(badScope,my)"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test scope is an invalid value when the request user is not admin
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void typeIsNonAdminScopeInvalid() throws Exception {
        // for non-admin, the scope filter should be ignored, no validation on it
        assertBadResponse("?filter=" + encode("scope=badScope"), status().isOk(), 200, MEMBER_SUPER_TOKEN, null);
    }

    /**
     * Test jwt token is already expired.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void tokenIsExpired() throws Exception {
        assertBadResponse("", status().isBadRequest(), 400, EXPIRED_TOKEN, "JWT Expired.");
    }

    /**
     * Test user id in jwt token is 0.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void userIdInTokenIs0() throws Exception {
        assertBadResponse("", status().isBadRequest(), 400, ZERO_USER_ID_TOKEN, "The user id should be positive.");
    }

    /**
     * Test user id in jwt token is a string or something can't format.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void invalidUserIdToken() throws Exception {
        assertBadResponse("", status().isBadRequest(), 400, INVALID_USER_ID_TOKEN, "Invalid user id.");
    }

    /**
     * Test user id in jwt token is not existed.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void userIsNotExist() throws Exception {
        assertBadResponse("", status().isBadRequest(), 400, NON_EXIST_USER_ID_TOKEN, "The userId: 123 is not exist.");
    }

    /**
     * Test when there is no jwt token passed. An unauthorized error will be returned.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void unauthorized() throws Exception {
        assertBadResponse("", status().isUnauthorized(), 401, null, "Access Restricted.");
    }

    /**
     * Tests supported sort orders.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void supportedSortOrders() throws Exception {
        createRequest("?filter=" + encode("scope=my") + "&limit=1&orderBy=projectId asc nulls first",
                MEMBER_HEFFAN_TOKEN).andExpect(status().isOk());
        createRequest("?filter=" + encode("scope=my") + "&limit=1&orderBy=projectId desc nulls last",
                MEMBER_HEFFAN_TOKEN).andExpect(status().isOk());

        // should fail for other combinations
        createRequest("?filter=" + encode("scope=my") + "&limit=1&orderBy=projectId asc nulls last"
                , MEMBER_HEFFAN_TOKEN).andExpect(status().isBadRequest());
        createRequest("?filter=" + encode("scope=my") + "&limit=1&orderBy=projectId desc nulls first"
                , MEMBER_HEFFAN_TOKEN).andExpect(status().isBadRequest());

    }

    /**
     * Test order by.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void orderby_scenarios() throws Exception {
        createRequest("?filter=" + "limit=1&orderBy=projectId"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000001")));

        createRequest("?filter=" + "limit=1&orderBy=projectId des null last"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000021")));

        createRequest("?filter=" + "limit=1&orderBy=projectName"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000002")));

        createRequest("?filter=" + "limit=1&orderBy=projectStatus"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000020")));

        createRequest("?filter=" + "limit=1&orderBy=projectCreatedDate"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000021")));

        createRequest("?filter=" + "limit=1&orderBy=projectUpdatedDate"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id", is("30000021")));

        // try sorting by not supported fileds
        createRequest("?filter=" + "limit=1&orderBy=billingId"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest());

        createRequest("?filter=" + "limit=1&orderBy=projectCreatedBy"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest());

    }

    /**
     * Test passing string filters with single quotes.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void quotedStringSupport() throws Exception {
        createRequest("?filter=" + encode("projectStatus='active'") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        createRequest("?filter=" + encode("projectStatus=in('active', 'completed')") + "&limit=3"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));

        // now a quoted leading space should cause a problem
        createRequest("?filter=" + encode("scope=' all'") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message").value(Matchers.containsString("Invalid scope")));

        // should validate the first one
        createRequest("?filter=" + encode("scope=in(' all ', 'my')") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message").value(Matchers.containsString("Invalid scope")));


        // so should ignore the second one
        createRequest("?filter=" + encode("scope=in('all', 'my ')") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
    }

    /**
     * Test the scope filter for admin .
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void scope_scenario_1() throws Exception {
        createRequest("?filter=" + encode("scope=my") + "&limit=100&orderBy=projectId"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(19)));
    }

    /**
     * Test the scope filter for admin
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void scope_scenario_2() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(21)));

    }

    /**
     * Test the scope filter for non-admin.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void scope_scenario_3() throws Exception {
        // for non-admin, the scope filter does not work
        createRequest("?filter=" + encode("scope=my") + "&limit=100&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(17)));
    }

    /**
     * Test the scope filter for non-admin.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void scope_scenario_4() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(17)));
    }


    /**
     * Test the limit and offset combination.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limit_offset_scenario_1() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=1&offset=0&&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
    }

    /**
     * Test the limit and offset combination.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limit_offset_scenario_2() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=1&offset=1&&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
    }


    /**
     * Test the limit and offset combination.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limit_offset_scenario_3() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=1&offset=1000&&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
    }


    /**
     * Test the limit and offset combination.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limit_offset_scenario_4() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=4&offset=10&&orderBy=projectId"
                , MEMBER_SUPER_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(4)));
    }

    /**
     * Test the limit and offset combination.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void limit_offset_scenario_5() throws Exception {

        createRequest("?filter=" + encode("scope=all") + "&limit=1000&offset=1&&orderBy=projectId"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(20)));
    }


    /**
     * The success scenario 1 to test the nameLink filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_1() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_1.json",
                createRequest("?filter=" + encode("scope=all&nameLike=in('*Mobile*','*API*')"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 2 to test the name filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_2() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_2.json",
                createRequest("?filter=" + encode("scope=all&name='Client 30000001 Billing Account 1 Web Application Project 1'"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 3 to test the project Id filter on user 'heffan' (admin)
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_3() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_3.json",
                createRequest("?filter=" + encode("scope=all&id=in(30000005,30000014,30000018)"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 4 to test the project Id filter on normal user 'super'.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_4() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_4.json",
                createRequest("?filter=" + encode("scope=all&id=in(30000005,30000014,30000018)"), MEMBER_SUPER_TOKEN));
    }

    /**
     * The success scenario 5 to billing account name exact match filter
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_5() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_5.json",
                createRequest("?filter=" + encode("scope=my&billingName='Client 30000001 Major Billing Account 1'"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 6 to billing account name partical match filter
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_6() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_6.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&billingNameLike=in('*Secret*')"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 7 to billing account id filter
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_7() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_7.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&billingId=in('30000007')"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 8 to project status filter
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_8() throws Exception {

        assertResponse("test/expected/my_projects/expected_get_my_projects_8.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&projectStatus=in('active', 4)"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 9 to test multiple filter and order combination
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_9() throws Exception {

        assertResponse("test/expected/my_projects/expected_get_my_projects_9.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&projectStatus='Completed'&billingNameLike='*[Ss]*'" + "&orderBy=" + encode("projectName dec null last")), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 10 to test multiple filter and order combination
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_10() throws Exception {

        assertResponse("test/expected/my_projects/expected_get_my_projects_10.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&billingNameLike='*Major*'&nameLike='*[Ww]eb*'" + "&orderBy=" + encode("projectId")), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 11 to test multiple filter and order, limit, offset combination
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_11() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_11.json",
                createRequest("?metadata=true&limit=1&offset=1&filter=" + encode("billingNameLike='*Major*'&nameLike='*Mobile*'" + "&orderBy=" + encode("projectId")), MEMBER_SUPER_TOKEN));
    }

    /**
     * The success scenario 12 to group permission. dok_tester does not have user_permission_grant.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_12() throws Exception {
        clearUserPermissionGrant();

        activateGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(19)));

        deactiveGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
    }

    /**
     * The success scenario 14 to test multiple filter and order, limit, offset combination
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_13() throws Exception {
        assertResponse("test/expected/my_projects/expected_get_my_projects_13.json",
                createRequest("?metadata=true&limit=10&offset=0&filter=" + encode("billingName='Client 30000001 Minor Billing Account 2'" + "&orderBy=" + encode("projectId")), MEMBER_SUPER_TOKEN));
    }


    /**
     * The success scenario 8 to project status filter
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_14() throws Exception {

        assertResponse("test/expected/my_projects/expected_get_my_projects_14.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&projectStatus=in('ACTIVe', 4)"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The success scenario 9 to test multiple filter and order combination
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_15() throws Exception {

        assertResponse("test/expected/my_projects/expected_get_my_projects_15.json",
                createRequest("?metadata=true&limit=30&filter=" + encode("scope=my&projectStatus='COMpleTed'&billingNameLike='*[Ss]*'" + "&orderBy=" + encode("projectName dec null last")), MEMBER_HEFFAN_TOKEN));
    }


    /**
     * The success scenario 16 to test the group associated direct projects
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_16() throws Exception {
        clearUserPermissionGrant();

        activateGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(19)));

        deactiveGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        deleteNewGroup();
        createNewGroup();

        insertGroupProjects();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
        assertResponse("test/expected/my_projects/expected_get_my_projects_16.json", createRequest("?metadata=true&filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN));

        // delete the new group at the end to not break existing test data
        deleteNewGroup();
    }

    /**
     * The success scenario 17 to test the group associated billing accounts
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_17() throws Exception {
        clearUserPermissionGrant();

        activateGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(19)));

        deactiveGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        deleteNewGroup();
        createNewGroup();

        insertGroupBillings();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
        assertResponse("test/expected/my_projects/expected_get_my_projects_17.json", createRequest("?metadata=true&filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN));

        // delete the new group at the end to not break existing test data
        deleteNewGroup();
    }


    /**
     * The success scenario 18 to test the user_permission_grant
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_18() throws Exception {
        clearUserPermissionGrant();

        activateGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(19)));

        deactiveGroupMember();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        deleteNewGroup();
        insertUserPermissionGrant();

        createRequest("?filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
        assertResponse("test/expected/my_projects/expected_get_my_projects_18.json", createRequest("?metadata=true&filter=" + encode("scope=all") + "&limit=100&orderBy=projectId"
                , MEMBER_DOK_TOKEN));

        clearUserPermissionGrant();
    }



    /**
     * Deactive the group member of dok_tester
     */
    private void deactiveGroupMember() {
        tcsCatalogJdbcTemplate.update("update group_member set active = 0 WHERE user_id = 20", EMPTY_MAP);
    }

    /**
     * Activate the group member of dok_tester
     */
    private void activateGroupMember() {
        tcsCatalogJdbcTemplate.update("update group_member set active = 1 WHERE user_id = 20", EMPTY_MAP);
    }

    /**
     * Clear the user's permissions in user_permission_grant
     */
    private void clearUserPermissionGrant() {
        tcsCatalogJdbcTemplate.update("DELETE FROM user_permission_grant WHERE user_id = 20", EMPTY_MAP);
    }

    /**
     * Insert a record into user_permission_grant for dok_tester
     */
    private void insertUserPermissionGrant() {
        tcsCatalogJdbcTemplate.update("insert into user_permission_grant VALUES(40000000, 20, 30000019, 2, 0);", EMPTY_MAP);
    }

    /**
     * Create a new customer group and assign dok_tester as group member.
     */
    private void createNewGroup() {
        tcsCatalogJdbcTemplate.update("INSERT INTO customer_group VALUES(40000000, 'New Group for client 30000003', 'WRITE', 30000003, 0, null, null, 0)", EMPTY_MAP);
        tcsCatalogJdbcTemplate.update("INSERT INTO group_member(group_member_id, user_id, group_id, specific_permission, active, activated_on, use_group_default)  \n" +
                "VALUES(2000, 20, 40000000, null, 1, '2014-12-29 13:43:21', 1)", EMPTY_MAP);
    }

    /**
     * Delete all the data related to the created new group.
     */
    private void deleteNewGroup() {
        tcsCatalogJdbcTemplate.update("delete from group_member where group_id = 40000000", EMPTY_MAP);
        tcsCatalogJdbcTemplate.update("delete from group_associated_direct_projects where group_id = 40000000", EMPTY_MAP);
        tcsCatalogJdbcTemplate.update("delete from group_associated_billing_accounts where group_id = 40000000", EMPTY_MAP);
        tcsCatalogJdbcTemplate.update("delete from customer_group where group_id = 40000000", EMPTY_MAP);
    }

    /**
     * Insert a new direct project for the newly created group.
     */
    private void insertGroupProjects() {
        tcsCatalogJdbcTemplate.update("insert into group_associated_direct_projects VALUES(2000, 40000000, 30000020)", EMPTY_MAP);
    }

    /**
     * Insert a new billing for the newly created group.
     */
    private void insertGroupBillings() {
        tcsCatalogJdbcTemplate.update("insert into group_associated_billing_accounts VALUES(40000000, 30000007)", EMPTY_MAP);
    }
}
