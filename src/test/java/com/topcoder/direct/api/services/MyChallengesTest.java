/*
 * Copyright (C) 2014 - 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import com.topcoder.direct.rest.BaseDirectAPITest;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
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
 * This tests ChallengeService class.
 * </p>
 *
 *
 * <p>
 * Version 1.1 (TopCoder Direct API - Project Retrieval API)
 * - Refactor the common methods to BaseDirectAPITest.
 *
 * <p>
 * Version 1.2 (Direct API - Fix Challenges API Integration Tests)
 * <ul>
 *     <li>Fixed the existing test cases</li>
 *     <li>Updated the existing test cases to test metadata</li>
 *     <li>Added test cases for metadata </li>
 *     <li>Added test cases for start/end date filters</li>
 *     <li>Fixed the test data setup logic, only setup test data when the tests begin,
 *     and clear test data after all the tests finished running</li>
 * </ul>
 * </p>
 *
 * <p>
 * Version 1.3 (POC Assembly - Direct API Create direct project)
 * <ul>
 *     <li>Removed unuses test case on support method post</li>
 * </ul>
 * </p>
 *
 * @author j3_guile, GreatKevin
 * @version 1.3
 */
public class MyChallengesTest extends BaseDirectAPITest {

    /**
     * The base url of this test.
     */
    private static final String BASE_URL = "/api/v2/challenges";

    /**
     * The number of tests have been run.
     *
     * @since 1.2
     */
    private static int currentRunningCount = 0;

    /**
     * The total tests count of MyChallengesTest
     *
     * @since 1.2
     */
    private static int testsCount = -1;

    /**
     * Counts the number of tests in this junit test class.
     *
     * @return the number of tests.
     *
     * @since 1.2
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
     * This method will insert test data into topcoder database.
     *
     * @throws IOException if any error occurred during reading file.
     */
    public void insertTestData() throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        timeOltpJdbcTemplate.update(readFile("test/data/my_created_challenges/time_oltp__insert.sql"), map);
        corporateOltpJdbcTemplate.update(readFile("test/data/my_created_challenges/corporate_oltp__insert.sql"),
            map);
        loadDataFromFile("test/data/my_created_challenges/tcs_catalog__insert_1.sql", tcsCatalogJdbcTemplate);
        loadDataFromFile("test/data/my_created_challenges/tcs_dw__insert.sql", tcsDwJdbcTemplate);
        tcsCatalogJdbcTemplate.update(readFile("test/data/my_created_challenges/tcs_catalog__insert_2.sql"),
            map);
        tcsCatalogJdbcTemplate.update(
            "INSERT INTO corporate_oltp\\:direct_project_account (project_id, direct_project_account_id,"
                + " billing_account_id) VALUES (40005501, 100000, 100000)", map);
        tcsCatalogJdbcTemplate.update(
            "INSERT INTO corporate_oltp\\:direct_project_account (project_id, direct_project_account_id,"
                + " billing_account_id) VALUES (40005501, 100001, 40005502)", map);
    }

    /**
     * Setup the env.
     *
     * @throws IOException if any error occurred during reading file.
     */
    @Before
    public void setup() throws IOException {
        if(currentRunningCount == 0) {
            // if no tests have been run yet, setup the test data
            clearMyChallengeGroupsData();
            cleanTestData();
            insertTestData();
        }
        currentRunningCount++;
    }

    /**
     * This method will delete all the test data insert into topcoder database in this test file.
     *
     * @throws IOException if any error occurred during reading file.
     */
    public void cleanTestData() throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        tcsCatalogJdbcTemplate.update(
            "DELETE FROM corporate_oltp\\:direct_project_account WHERE direct_project_account_id >= 100000", map);
        tcsDwJdbcTemplate.update(readFile("test/data/my_created_challenges/tcs_dw__clean.sql"), map);
        tcsCatalogJdbcTemplate
            .update(readFile("test/data/my_created_challenges/tcs_catalog__clean.sql"), map);
        corporateOltpJdbcTemplate.update(readFile("test/data/my_created_challenges/corporate_oltp__clean.sql"),
            map);
        timeOltpJdbcTemplate.update(readFile("test/data/my_created_challenges/time_oltp__clean.sql"), map);
    }

    /**
     * Tear down the env.
     *
     * @throws IOException if any error occurred during reading file.
     */
    @After
    public void tearDown() throws IOException {
        if(currentRunningCount == countTests()) {
            //clear test data after all the tests have been run
            clearMyChallengeGroupsData();
            cleanTestData();
        }
    }

    /**
     * Clears data specifically created for this test.
     */
    private void clearMyChallengeGroupsData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        tcsCatalogJdbcTemplate.update("DELETE FROM user_permission_grant WHERE user_id = 20", map);
        tcsCatalogJdbcTemplate.update("DELETE FROM group_associated_direct_projects WHERE group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("DELETE FROM group_associated_billing_accounts WHERE group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("DELETE FROM group_member WHERE group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("DELETE FROM customer_group WHERE group_id = 100000", map);
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
     * Test direct project id a negative integer.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsNegative() throws Exception {
        assertBadResponse("?filter=" + encode("directProjectId=-2"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
            null);
    }

    /**
     * Test direct project id is 0.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsZero() throws Exception {
        assertBadResponse("?filter=" + encode("directProjectId=0"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
            null);
    }

    /**
     * Test direct project id is a double value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsDouble() throws Exception {
        assertBadResponse("?filter=" + encode("directProjectId=2.234"), status().isBadRequest(), 400,
            MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test direct project id is a string value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void directProjectIdIsString() throws Exception {
        assertBadResponse("?filter=" + encode("directProjectId=abc"), status().isBadRequest(), 400,
            MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test type is a invalid value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void typeIsInvalid() throws Exception {
        assertBadResponse("?filter=" + encode("type=abc"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN, null);
    }

    /**
     * Test type is a invalid value mixed with values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void typeIsInvalid_2() throws Exception {
        assertBadResponse("?filter=" + encode("type=in(active,completed)"), status().isBadRequest(), 400,
            MEMBER_HEFFAN_TOKEN, null);
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
     * Test when there caller is not the creator.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void callerNotSameAsCreator() throws Exception {
        assertBadResponse("?filter=" + encode("creator=super"), status().isBadRequest(), 400, MEMBER_HEFFAN_TOKEN,
                "Invalid creator, only current user is supported.");
    }

    /**
     * Test when there caller is not the creator with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void callerNotSameAsCreator_2() throws Exception {
        assertBadResponse("?filter=" + encode("creator=in(super, heffan)"), status().isBadRequest(), 400,
            MEMBER_HEFFAN_TOKEN, "Invalid creator, only current user is supported.");
    }

    /**
     * Tests supported sort orders.
     * informix uses ASC_NULLS_FIRST and DESC_NULLS_LAST
     *
     * Note: currently the direction is always being set to desc null first when provided
     *
     * @throws Exception if any error occurred.
     */
    // @Test
    public void supportedSortOrders() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id asc nulls first",
                MEMBER_HEFFAN_TOKEN).andExpect(status().isOk());
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id desc nulls last",
                MEMBER_HEFFAN_TOKEN).andExpect(status().isOk());

        // should fail for other combinations
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id asc nulls last"
                , MEMBER_HEFFAN_TOKEN).andExpect(status().isBadRequest());
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id desc nulls first"
            , MEMBER_HEFFAN_TOKEN).andExpect(status().isBadRequest());

    }

    /**
     * Test order by ascending value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void orderby_scenario_d1() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("31")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeName"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005504")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeType"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005728")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=clientName"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=clientId"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=billingName"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=billingId"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=directProjectName"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=directProjectId"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeStartDate"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005740")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeEndDate"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005740")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=drPoints"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeStatus"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005736")));

        // try sorting by lists
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeTechnologies"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isBadRequest());
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengePlatforms"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isBadRequest());
        // try sorting by nested object
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=memberPrize"
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
        createRequest("?filter=" + encode("creator='heffan'") + "&limit=1"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        createRequest("?filter=" + encode("creator=in('heffan', heffan)") + "&limit=1"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        // now a quoted leading space should cause a problem
        createRequest("?filter=" + encode("creator=' heffan'") + "&limit=1"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result.content.message").value("Invalid creator, only current user is supported."));

        // so should a trailing space
        createRequest("?filter=" + encode("creator=in(heffan, 'heffan ')") + "&limit=1"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.result.content.message").value("Invalid creator, only current user is supported."));
    }

    /**
     * Test order by descending.
     *
     * Note: Need to update when the direction is properly set in the framework
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void orderby_scenario_2() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id desc nulls last"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005740")));
    }

    /**
     * Test order by desc null first, which is not supported.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void orderby_scenario_3() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id desc nulls first"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Specified sort order is not supported")));
    }

    /**
     * Test order by  asc nulls last, which is not supported.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void orderby_scenario_4() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id asc nulls last"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Specified sort order is not supported")));
    }

    /**
     * The default scenario.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_1() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1.json",
            createRequest("?filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * The default scenario with metadata.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_1_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengeType filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_2() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_2.json",
            createRequest("?filter=" + encode("creator=heffan&challengeType=development"), MEMBER_HEFFAN_TOKEN));
    }


    /**
     * Test challengeType filter with metadata
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_2_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_2_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&challengeType=development"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengeType filter. Add multiple challenge type in it.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_3() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_3.json",
            createRequest("?filter=" + encode("creator=heffan&challengeType=in(development, design)"),
                MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengeType filter. Add multiple challenge type in it with metadata set to true
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_3_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_3_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&challengeType=in(development, design)"),
                        MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test type filter. The type is active
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_4() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_4.json",
            createRequest("?filter=" + encode("creator=heffan&type=active"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test type filter. The type is active with metadata support.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_4_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_4_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&type=active"), MEMBER_HEFFAN_TOKEN));
    }


    /**
     * Test type filter. The type is past
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_5() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_5.json",
            createRequest("?filter=" + encode("creator=heffan&type=past"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test type filter. The type is past with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_5_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_5_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&type=past"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test type filter. The type is draft.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_6() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_6.json",
            createRequest("?filter=" + encode("creator=heffan&type=draft"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test type filter. The type is draft with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_6_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_6_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&type=draft"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test directProjectId filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_7() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_7.json",
            createRequest("?filter=" + encode("creator=heffan&directProjectId=40005515"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test directProjectId filter with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_7_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_7_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&directProjectId=40005515"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test directProjectName filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_8() throws Exception {
        assertResponse(
            "test/expected/my_challenges/expected_get_my_challenges_8.json",
            createRequest("?filter="
                + encode("creator=heffan&directProjectName=Client 40005501 Billing Account 2 Project 1"),
                MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengeTechnologies filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_9() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_9.json",
            createRequest("?filter=" + encode("creator=heffan&challengeTechnologies=j2ee"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengeTechnologies filter with metadata support.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_9_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_9_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&challengeTechnologies=j2ee"), MEMBER_HEFFAN_TOKEN));
    }


    /**
     * Test challengePlatforms filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_10() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_10.json",
            createRequest("?filter=" + encode("creator=heffan&challengePlatforms=nodejs"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challengePlatforms filter with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_10_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_10_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan&challengePlatforms=nodejs"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test offset filter. The offset is 10.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_11() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_11.json",
            createRequest("?filter=" + encode("creator=heffan") + "&limit=10&offset=10", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test offset filter. The offset is 10 with metadata support.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_11_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_11_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=10&offset=10", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit filter. The limit is -1 so the pageSize should be max value of integer.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_12() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_12.json",
            createRequest("?filter=" + encode("creator=heffan") + "&limit=-1", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit filter. The limit is -1 so the pageSize should be max value of integer with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_12_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_12_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=-1", MEMBER_HEFFAN_TOKEN));

        createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=-1", MEMBER_HEFFAN_TOKEN).andExpect(status().isOk()).andExpect(
                jsonPath("$.result.content", Matchers.hasSize(268))).andExpect(jsonPath("$.result.metadata.totalCount", is(268)));
    }

    /**
     * Test limit filter. The limit is 2.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_13() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_13.json",
                createRequest("?filter=" + encode("creator=heffan") + "&limit=2", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit filter. The limit is 2 with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_13_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_13_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=2", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit and offset filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_14() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_14.json",
            createRequest("?filter=" + encode("creator=heffan") + "&limit=2&offset=0", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit and offset filter with metadata support
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_14_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_14_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=2&offset=0", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit and offset filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_15() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_15.json",
            createRequest("?filter=" + encode("creator=heffan") + "&limit=2&offset=2", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test limit and offset filter with metadata support.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_15_metadata() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_15_metadata.json",
                createRequest("?metadata=true&filter=" + encode("creator=heffan") + "&limit=2&offset=2", MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test another user.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_16() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_16.json",
            createRequest("?filter=" + encode("creator=super"), MEMBER_SUPER_TOKEN));
    }

    /**
     * Test challenge status id filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_17() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_4.json",
            createRequest("?filter=" + encode("creator=heffan&challengeStatus=1"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challenge status name filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_18() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_4.json",
            createRequest("?filter=" + encode("creator=heffan&challengeStatus=aCtIve"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challenge status id filter and type filter at same time.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_19() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_4.json",
            createRequest("?filter=" + encode("creator=heffan&challengeStatus=1&type=active"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challenge status id filter and type filter at same time. The type and challenge status are conflict.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_20() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_17.json",
                createRequest("?filter=" + encode("creator=heffan&challengeStatus=1&type=past"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test challenge status name filter and type filter at same time. The type and challenge status are conflict.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_21() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_17.json",
                createRequest("?filter=" + encode("creator=heffan&challengeStatus=active&type=past"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * This sets a baseline for the next set of access checking tests.
     *
     * Test non-accessible project for 'super'.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_22() throws Exception {
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
                jsonPath("$.result.content", Matchers.hasSize(0)));
    }

    /**
     * Checks grants made directly in user_permission_grant.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_23() throws Exception {
        setupUserGrant_1();
        String content = createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10))).andReturn().getResponse()
            .getContentAsString();
        verifyDirectId(content, 40005501);
        deleteUserGrant_1();
    }

    /**
     * Verifies that all the records returned have the given tc direct project Id.
     *
     * @param content the records returned
     * @param directId the expected ID
     * @throws JSONException for any errors encountered
     */
    private void verifyDirectId(String content, int directId) throws JSONException {
        JSONObject resultObj = new JSONObject(content);
        JSONArray records = resultObj.getJSONObject("result").getJSONArray("content");
        for (int i = 0; i < records.length(); i++) {
            Assert.assertEquals(directId, records.getJSONObject(i).getInt("directProjectId"));
        }
    }

    /**
     * Checks grants made via group_associated_direct_projects.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_24() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        setupGroupMembership();
        tcsCatalogJdbcTemplate.update(
                "INSERT INTO group_associated_direct_projects(group_direct_project_id, group_id, "
                        + "tc_direct_project_id) VALUES (100000, 100000, 40005501)", map);
        String content = createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10))).andReturn().getResponse()
            .getContentAsString();
        verifyDirectId(content, 40005501);

        deactivateMember();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
            jsonPath("$.result.content", Matchers.hasSize(0)));

        activateMember();
        archiveTestGroup();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
            jsonPath("$.result.content", Matchers.hasSize(0)));
        deleteGroupMembership();
    }

    /**
     * Checks grants made via group_associated_billing_accounts.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_25() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        setupGroupMembership();
        tcsCatalogJdbcTemplate.update(
                "INSERT INTO group_associated_billing_accounts(group_id, billing_account_id) VALUES (100000, 100000)", map);
        String content = createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10))).andReturn().getResponse()
            .getContentAsString();
        verifyDirectId(content, 40005501);

        deactivateMember();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
                jsonPath("$.result.content", Matchers.hasSize(0)));

        activateMember();
        archiveTestGroup();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
            jsonPath("$.result.content", Matchers.hasSize(0)));
        deleteGroupMembership();
    }

    /**
     * Checks grants made via auto grants on tt_client_project.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_26() throws Exception {
        setupClientGroup();
        String content = createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10))).andReturn().getResponse()
            .getContentAsString();
        verifyDirectId(content, 40005501);

        deactivateMember();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
                jsonPath("$.result.content", Matchers.hasSize(0)));

        activateMember();
        archiveTestGroup();
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk()).andExpect(
                jsonPath("$.result.content", Matchers.hasSize(0)));
        deleteGroupMembership();
    }

    /**
     * Checks grants made directly in user_permission_grant, try different permission types.
     * Allowed are 1, 2 , 3.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_27() throws Exception {
        setupUserGrant_1();

        tcsCatalogJdbcTemplate.update(
                "UPDATE user_permission_grant set permission_type_id = 2 WHERE user_permission_grant_id = 40005573",
                new HashMap<String, Object>());
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10)));

        tcsCatalogJdbcTemplate.update(
                "UPDATE user_permission_grant set permission_type_id = 3 WHERE user_permission_grant_id = 40005573",
                new HashMap<String, Object>());
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10)));

        tcsCatalogJdbcTemplate.update(
                "UPDATE user_permission_grant set permission_type_id = 4 WHERE user_permission_grant_id = 40005573",
                new HashMap<String, Object>());
        createRequest("?limit=1000", getNoProjectMember()).andExpect(status().isOk())
        .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
        deleteUserGrant_1();
    }

    /**
     * Test challenge status id filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_28() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeStatus=in(1)"), MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(50)));
        // same id
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeStatus=in(1, active)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(50)));
        // add drafts (35)
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeStatus=in(1,2)"), MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(85)));
        // active + completed
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeStatus=in(1,completed)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(170)));
        // add drafts
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeStatus=in(1,draft,completed)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(205)));
    }

    /**
     * Test direct project id filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_29() throws Exception {
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&directProjectId=in(40005515)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(9))).andExpect(jsonPath("$.result.metadata.totalCount", is(9)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&directProjectId= IN ( 40005515, 40005524)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(19))).andExpect(jsonPath("$.result.metadata.totalCount", is(19)));
    }

    /**
     * Test type filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_30() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&type=in(past)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(183)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&type= IN ( active, draft)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(85))).andExpect(jsonPath("$.result.metadata.totalCount", is(85)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&type=in(past, active, draft)")
                , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));
    }

    /**
     * Test direct project name filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_31() throws Exception {
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&directProjectName=in(Client 40005501)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(115)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(115)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&directProjectName=in(Client 40005502)")
                , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(116)));
        createRequest("?limit=1000&filter="
            + encode("creator=heffan&directProjectName=in(Client 40005502,Client 40005501)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(231)));
    }

    /**
     * Test challengePlatforms filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_32() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(nodejs)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(google)")
                , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(google, nodejs)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(2)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(google, nodejs, heroku)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)));
    }

    /**
     * Test challengeTechnologies filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_33() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(j2ee)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(41)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(jsf)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(3)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(j2ee, jsf)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(44)));
    }

    /**
     * Test challengePlatforms filter with unmatched lookup value.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_34() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(awesomeplatform)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
    }

    /**
     * Test challengeTechnologies filter with unmatched lookup values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_35() throws Exception {
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(awesometech)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(0)));
    }

    /**
     * Test clientId filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_36() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=40005501")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].clientId", is(40005501)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(115)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&clientId=40005502")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].clientId", is(40005502)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(116)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(116)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=in(40005501,40005502)")
                , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(231)));
    }

    /**
     * Test billingId filter.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_37() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&billingId=40005502")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].billingId", is(40005502)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(57)));
        createRequest("?metadata=true&limit=1000&filter=" + encode("creator=heffan&billingId=40005504")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].billingId", is(40005504)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(58)))
            .andExpect(jsonPath("$.result.metadata.totalCount", is(58)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=in(40005502,40005504)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(116)));
    }

    /**
     * Test the API with metadata set to false.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testMetadataFalse() throws Exception {
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1.json",
                createRequest("?metadata=false&filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test the API with metadata set to arbitrary value except true.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testMetadataArbitraryValues() throws Exception {
        // should treat these values as false
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1.json",
                createRequest("?metadata=xxx&filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1.json",
                createRequest("?metadata=&filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
        assertResponse("test/expected/my_challenges/expected_get_my_challenges_1.json",
                createRequest("?metadata=[]&filter=" + encode("creator=heffan"), MEMBER_HEFFAN_TOKEN));
    }

    /**
     * Test the startDateFrom filter with invalid values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testInvalidStartDateFrom() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=xx/11/2014") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=null") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=abcdef") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
    }

    /**
     * Tests the startDateTo filter with invalid values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testInvalidStartDateTo() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&startDateTo=xx/11/2014") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
        createRequest("?filter=" + encode("creator=heffan&startDateTo=null") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
        createRequest("?filter=" + encode("creator=heffan&startDateTo=abcdef") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge start date filter")));
    }

    /**
     * Tests the endDateFrom with invalid values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testInvalidEndDateFrom() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=xx/11/2014") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=null") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=abcdef") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
    }

    /**
     * Tests the endDateTo with invalid values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testInvalidEndDateTo() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&endDateTo=xx/11/2014") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
        createRequest("?filter=" + encode("creator=heffan&endDateTo=null") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
        createRequest("?filter=" + encode("creator=heffan&endDateTo=abcdef") + "&limit=1"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result.content.message", Matchers.containsString("Invalid challenge end date filter")));
    }


    /**
     * Tests the startDateFrom filter only accuracy.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testStartDateFrom() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/01/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/02/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 200);

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=01/01/2015") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.result.content[0].id", is("40005617")));

        pushBackProjectPhases(40005617, 200);
    }

    /**
     * Tests the endDateFrom filter only accuracy.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testEndDateFrom() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/21/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 1);

        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/21/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.result.content[0].id", is("40005617")));

        pushBackProjectPhases(40005617, 1);
    }

    /**
     * Tests the startDateTo filter only accuracy.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testStartDateTo() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&startDateTo=08/01/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        createRequest("?filter=" + encode("creator=heffan&startDateTo=07/31/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 200);

        createRequest("?filter=" + encode("creator=heffan&startDateTo=02/16/2015") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(267)));

        pushBackProjectPhases(40005617, 200);
    }

    /**
     * Tests the endDateTo filter only accuracy.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testEndDateTo() throws Exception {
        createRequest("?filter=" + encode("creator=heffan&endDateTo=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        createRequest("?filter=" + encode("creator=heffan&endDateTo=08/19/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, -1);

        createRequest("?filter=" + encode("creator=heffan&endDateTo=08/19/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        pushBackProjectPhases(40005617, -1);
    }

    /**
     * Accuracy tests of the startDateTo and startDateFrom filters together.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testStartDateToAndFrom() throws Exception {
        // startDateFrom 08/01/2014 00:00:00 -> startDateTo 08/01/2014 23:59:59
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/01/2014&startDateTo=08/01/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        // startDateFrom > startDateTo, the result should be 0
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/01/2014&startDateTo=07/31/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 1);
        advanceProjectPhases(40005700, 2);
        advanceProjectPhases(40005735, 3);

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/02/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/03/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/04/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        pushBackProjectPhases(40005617, 1);
        pushBackProjectPhases(40005700, 2);
        pushBackProjectPhases(40005735, 3);

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/02/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/03/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/04/2014&startDateTo=08/04/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
    }

    /**
     * Accuracy tests of the endDateTo and endDateFrom filters together.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testEndDateToAndFrom() throws Exception {
        // endDateFrom 08/20/2014 00:00:00 -> endDateTo 08/20/2014 23:59:59
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/20/2014&endDateTo=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        // endDateFrom > endDateTo, the result should be 0
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/21/2014&endDateTo=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 1);
        advanceProjectPhases(40005700, 2);
        advanceProjectPhases(40005735, 3);

        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/21/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/22/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/23/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)));

        pushBackProjectPhases(40005617, 1);
        pushBackProjectPhases(40005700, 2);
        pushBackProjectPhases(40005735, 3);

        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/21/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/22/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
        createRequest("?filter=" + encode("creator=heffan&endDateFrom=08/23/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
    }

    /**
     * Accuracy tests of the startDateTo, startDateFrom, endDateTo and endDateFrom filters together.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void testStartDateEndDateFromTo() throws Exception {
        // startDateFrom 08/01/2014 00:00:00 -> startDateTo 08/01/2014 23:59:59
        // endDateFrom 08/20/2014 00:00:00 -> endDateTo 08/20/2014 23:59:59
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/01/2014&startDateTo=08/01/2014&endDateFrom=08/20/2014&endDateTo=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(268)));

        // startDateFrom 08/01/2014 00:00:00 -> startDateTo 08/01/2014 23:59:59
        // endDateFrom 08/21/2014 00:00:00 -> endDateTo 08/21/2014 23:59:59
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/01/2014&startDateTo=08/01/2014&endDateFrom=08/21/2014&endDateTo=08/21/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        // startDateFrom 08/02/2014 00:00:00 -> startDateTo 08/02/2014 23:59:59
        // endDateFrom 08/20/2014 00:00:00 -> endDateTo 08/20/2014 23:59:59
        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/02/2014&startDateTo=08/02/2014&endDateFrom=08/20/2014&endDateTo=08/20/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));

        advanceProjectPhases(40005617, 1);
        advanceProjectPhases(40005700, 2);
        advanceProjectPhases(40005735, 3);


        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/02/2014&startDateTo=08/04/2014&endDateFrom=08/21/2014&endDateTo=08/22/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].id", is("40005617")))
                .andExpect(jsonPath("$.result.content[1].id", is("40005700")));

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/03/2014&startDateTo=08/04/2014&endDateFrom=08/21/2014&endDateTo=08/22/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.result.content[0].id", is("40005700")));

        createRequest("?filter=" + encode("creator=heffan&startDateFrom=08/03/2014&startDateTo=08/04/2014&endDateFrom=08/21/2014&endDateTo=08/23/2014") + "&limit=1000"
                , MEMBER_HEFFAN_TOKEN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].id", is("40005700")))
                .andExpect(jsonPath("$.result.content[1].id", is("40005735")));

        pushBackProjectPhases(40005617, 1);
        pushBackProjectPhases(40005700, 2);
        pushBackProjectPhases(40005735, 3);
    }


    /**
     * Sets up a client group with the test user as member.
     */
    private void setupClientGroup() {
        tcsCatalogJdbcTemplate.update(
            "INSERT INTO customer_group(name, group_id, effective_group_id, default_permission,"
                + " client_id, auto_grant, archived_on, archived) VALUES "
                + "('cg1', 100000,  100000, 'read', 40005501, 1, null, 0)", new HashMap<String, Object>());
        tcsCatalogJdbcTemplate.update(
            "INSERT INTO group_member(activated_on, active, group_id, group_member_id, specific_permission,"
                + " unassigned_on, use_group_default, user_id) VALUES"
                + " (CURRENT, 1,  100000, 100000, 'read', null, 0, 20)", new HashMap<String, Object>());
    }

    /**
     * Sets up grant using user_permission_grant.
     */
    private void setupUserGrant_1() {
        String grant = "INSERT INTO user_permission_grant (user_permission_grant_id, user_id, "
            + "resource_id, permission_type_id, is_studio) VALUES (40005573, 20, 40005501, 1, 0)";
        tcsCatalogJdbcTemplate.update(grant, new HashMap<String, Object>());
    }

    /**
     * Delete user permission grant.
     */
    private void deleteUserGrant_1() {
        String delete = "DELETE FROM user_permission_grant WHERE user_permission_grant_id = 40005573";
        tcsCatalogJdbcTemplate.update(delete, new HashMap<String, Object>());
    }

    /**
     * Sets up a group with the test user as member.
     */
    private void setupGroupMembership() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        tcsCatalogJdbcTemplate.update(
            "INSERT INTO customer_group(name, group_id, effective_group_id, default_permission,"
                + "client_id, auto_grant, archived_on, archived) "
                + "VALUES ('cg1', 100000,  100000, 'read', 40005502, 0, null, 0)", map);
        tcsCatalogJdbcTemplate.update("INSERT INTO group_member(activated_on, active, group_id, group_member_id,"
            + "specific_permission, unassigned_on, use_group_default, user_id)"
            + "VALUES (CURRENT, 1,  100000, 100000, 'read', null, 0, 20)", map);
    }

    /**
     * Delete all the data related to the created new group.
     */
    private void deleteGroupMembership() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        tcsCatalogJdbcTemplate.update("delete from group_member where group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("delete from group_associated_direct_projects where group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("delete from group_associated_billing_accounts where group_id = 100000", map);
        tcsCatalogJdbcTemplate.update("delete from customer_group where group_id = 100000", map);
    }


    /**
     * Sets the test group member as inactive.
     */
    private void deactivateMember() {
        tcsCatalogJdbcTemplate.update("UPDATE group_member set active = 0 where user_id = 20",
            new HashMap<String, Object>());
    }

    /**
     * Sets the test group member as inactive.
     */
    private void activateMember() {
        tcsCatalogJdbcTemplate.update("UPDATE group_member set active = 1 where user_id = 20",
            new HashMap<String, Object>());
    }

    /**
     * Sets the test group as archived.
     */
    private void archiveTestGroup() {
        tcsCatalogJdbcTemplate.update(
            "UPDATE customer_group set archived = 1 WHERE name = 'cg1'",
            new HashMap<String, Object>());
    }

    /**
     * Move the phases time of the specified project forward the specified num of days
     *
     * @param projectId the project id
     * @param numOfDays the number of days.
     */
    private void advanceProjectPhases(int projectId, int numOfDays) {
        tcsCatalogJdbcTemplate.update(
                "update project_phase set \n" +
                        "fixed_start_time = fixed_start_time + " + numOfDays + " units day,\n" +
                        "scheduled_start_time = scheduled_start_time  + " + numOfDays + " units day,\n" +
                        "scheduled_end_time = scheduled_end_time +  " + numOfDays + " units day,\n" +
                        "actual_start_time = actual_start_time  + " + numOfDays + " units day,\n" +
                        "actual_end_time = actual_end_time  + " + numOfDays + " units day\n" +
                        "where project_id = " + projectId,
                new HashMap<String, Object>());
    }

    /**
     * Move the phases time of the specified project backward the specified num of days
     *
     * @param projectId the project id
     * @param numOfDays the number of days.
     */
    private void pushBackProjectPhases(int projectId, int numOfDays) {
        tcsCatalogJdbcTemplate.update(
                "update project_phase set \n" +
                        "fixed_start_time = fixed_start_time - " + numOfDays + " units day,\n" +
                        "scheduled_start_time = scheduled_start_time - " + numOfDays + " units day,\n" +
                        "scheduled_end_time = scheduled_end_time - " + numOfDays + " units day,\n" +
                        "actual_start_time = actual_start_time - " + numOfDays + " units day,\n" +
                        "actual_end_time = actual_end_time - " + numOfDays + " units day\n" +
                        "where project_id = " + projectId,
                new HashMap<String, Object>());
    }

    /**
     * Gets the base URL of the challenges API.
     *
     * @return the base URL of the challenges API.
     * @since 1.1
     */
    @Override
    protected String getBaseURL() {
        return BASE_URL;
    }
}
