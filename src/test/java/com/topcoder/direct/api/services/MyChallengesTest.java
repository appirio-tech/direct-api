/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.topcoder.direct.rest.BaseDirectAPITest;

/**
 * <p>
 * This tests ChallengeService class.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class MyChallengesTest extends BaseDirectAPITest {

    /**
     * The base url of this test.
     */
    private static final String BASE_URL = "/api/v2/challenges";

    /**
     * Flag for global initialization.
     */
    private static Boolean isInit = false;

    /**
     * Flag for global completion.
     */
    private static Boolean isDone = false;

    /**
     * The token is expired.
     */
    private static final String EXPIRED_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZHwxMzI0NTciLCJleHA"
        + "iOjE0MDU2NDk0NTUsImF1ZCI6IkNNYUJ1d1NuWTBWdTY4UExyV2F0dnZ1M2lJaUdQaDd0IiwiaWF0IjoxNDA1NTg5NDU1fQ.dGSEZK7N"
        + "3mJgcmAzYgm7HRKRW1pyQpi623LTJQm_T_E";

    /**
     * The token contains invalid userId "abc".
     */
    private static final String INVALID_USER_ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZHxhYmMiLCJ"
        + "leHAiOm51bGwsImF1ZCI6IkNNYUJ1d1NuWTBWdTY4UExyV2F0dnZ1M2lJaUdQaDd0IiwiaWF0IjoxNDA1OTM1NDAxfQ.lzfD9wKzPFSR"
        + "qcHHGtMeQ3YbsCuWIPo5rxdQcJD7yJA";

    /**
     * The token contains invalid userId 0.
     */
    private static final String ZERO_USER_ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZHwwIiwiZXhwIj"
        + "pudWxsLCJhdWQiOiJDTWFCdXdTblkwVnU2OFBMcldhdHZ2dTNpSWlHUGg3dCIsImlhdCI6MTQwNTkzNTQxMn0.IdCDq2IOXbu1Ho6Un_"
        + "64MDmua4tcU4Z9SrP5J8sB1bs";

    /**
     * The token contains non exist userId 123.
     */
    private static final String NON_EXIST_USER_ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZHwxMjMiL"
        + "CJleHAiOm51bGwsImF1ZCI6IkNNYUJ1d1NuWTBWdTY4UExyV2F0dnZ1M2lJaUdQaDd0IiwiaWF0IjoxNDA1OTM1NDIwfQ.XW6p88QLrc"
        + "AGDjrdckn8hlX4ucGCFO4wF3ClvlbKDqk";

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
        if (!isInit) {
            clearMyChallengeGroupsData();
            cleanTestData();
            insertTestData();
            isInit = true;
        }
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
        clearMyChallengeGroupsData();
        if (isDone) {
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
     * Create http request.
     *
     * @param url The url
     * @param token The jwt token passed to api.
     * @return The http request.
     * @throws Exception if any error occurred.
     */
    private ResultActions createRequest(String url, String token) throws Exception {
        return this.createGETRequest(BASE_URL + url, token);
    }

    /**
     * The helper method that used to perform failure test.
     *
     * @param url the url to call
     * @param status the expected HTTP status.
     * @param expectedStatus the content expected status.
     * @param token The jwt token passed to api.
     * @param expectedErrMsg The expected error message.
     * @throws Exception if any error occurred.
     */
    private void assertBadResponse(String url, ResultMatcher status, Integer expectedStatus, String token,
        String expectedErrMsg) throws Exception {
        ResultActions req = createRequest(url, token);
        req.andExpect(status);
        req.andExpect(jsonPath("$.result.status", is(expectedStatus)));
        if (isNotNullNorEmpty(expectedErrMsg)) {
            req.andExpect(jsonPath("$.result.content.message").value(expectedErrMsg));
        }
    }

    /**
     * Test the post method is not supported.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void methodNotSupported() throws Exception {
        this.mockMvc.perform(post(BASE_URL)).andExpect(status().isMethodNotAllowed());
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
    public void orderby_scenario_1() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("31")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeName"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("131")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeType"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("961")));

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
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeEndDate"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=drPoints"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("981")));

        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=challengeStatus"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("761")));

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
    //@Test
    public void orderby_scenario_2() throws Exception {
        createRequest("?filter=" + encode("creator=heffan") + "&limit=1&orderBy=id desc nulls first"
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].id", is("40005740")));
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
        createRequest("?limit=1000&filter=" + encode("creator=heffan&directProjectId=in(40005515)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(10)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&directProjectId= IN ( 40005515, 40005524)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(20)));
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
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(192)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&type= IN ( active, draft)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(85)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&type=in(past, active, draft)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(277)));
    }

    /**
     * Test direct project name filter with multiple values.
     *
     * @throws Exception if any error occurred.
     */
    @Test
    public void success_scenario_31() throws Exception {
        createRequest("?limit=1000&filter=" + encode("creator=heffan&directProjectName=in(Client 40005501)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(120)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&directProjectName=in(Client 40005502)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(120)));
        createRequest("?limit=1000&filter="
            + encode("creator=heffan&directProjectName=in(Client 40005502,Client 40005501)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(240)));
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
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(2)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(google, nodejs)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengePlatforms=in(google, nodejs, heroku)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
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
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(42)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(jsf)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(3)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(j2ee, jsf)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(45)));
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
        createRequest("?limit=1000&filter=" + encode("creator=heffan&challengeTechnologies=in(awesometech)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(0)));
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
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(120)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=40005502")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].clientId", is(40005502)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(120)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=in(40005501,40005502)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(240)));
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
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(60)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&billingId=40005504")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content[0].billingId", is(40005504)))
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(60)));
        createRequest("?limit=1000&filter=" + encode("creator=heffan&clientId=in(40005502,40005504)")
            , MEMBER_HEFFAN_TOKEN)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.content", Matchers.hasSize(120)));
        isDone = true;
        isInit = false;
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

}