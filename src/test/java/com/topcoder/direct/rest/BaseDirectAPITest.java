/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 *     The base test class for all the direct api test.
 *     This class also contains the helpful method for Test class.
 * </p>
 *
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/webapp/WEB-INF/direct-api-test-servlet.xml")
@WebAppConfiguration("file:src/test/webapp/WEB-INF/web.xml")
public abstract class BaseDirectAPITest {
    /**
     * The token for topcoder member "heffan".
     */
    @Value("${heffanToken}")
    protected String MEMBER_HEFFAN_TOKEN;

    /**
     * The token for topcoder member "super".
     */
    @Value("${superToken}")
    protected String MEMBER_SUPER_TOKEN;

    /**
     * The jdbc template for tcs_catalog database.
     */
    @Autowired
    @Qualifier("tcsCatalogJdbc")
    protected NamedParameterJdbcTemplate tcsCatalogJdbcTemplate;

    /**
     * The jdbc template for common_oltp database.
     */
    @Autowired
    @Qualifier("commonOltpJdbc")
    protected NamedParameterJdbcTemplate commonOltpJdbcTemplate;

    /**
     * The jdbc template for corporate_oltp database.
     */
    @Autowired
    @Qualifier("corporateOltpJdbc")
    protected NamedParameterJdbcTemplate corporateOltpJdbcTemplate;

    /**
     * The jdbc template for time_oltp database.
     */
    @Autowired
    @Qualifier("timeOltpJdbc")
    protected NamedParameterJdbcTemplate timeOltpJdbcTemplate;

    /**
     * The jdbc template for tcs_dw database.
     */
    @Autowired
    @Qualifier("tcsDwJdbc")
    protected NamedParameterJdbcTemplate tcsDwJdbcTemplate;

    /**
     * The mock mvc object used to test rest api.
     */
    protected MockMvc mockMvc;

    /**
     * The web application context.
     */
    @Autowired
    private WebApplicationContext wac;

    /**
     * The token for topcoder member "dok_tester".
     * A user configured with no access to any projects.
     *
     * @since 1.1
     */
    @Value("${testerToken}")
    private String noProjectMember;

    /**
     * The setup method of this test.
     * @throws Exception if any error occurred.
     */
    @Before
    public void beforeEach() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .build();
    }

    /**
     * Create http request.
     * @param url The url
     * @param token The jwt token passed to api.
     * @return The http request.
     * @throws Exception if any error occurred.
     */
    protected ResultActions createGETRequest(String url, String token) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(url);
        if (isNotNullNorEmpty(token)) {
            requestBuilder = requestBuilder.header("Authorization", "Bearer " + token);
        }
        return this.mockMvc.perform(requestBuilder);
    }

    /**
     * This method will read file from the classpath in test folder.
     * @param filePath - The relative path for file.
     * @return The file content.
     * @throws IOException if any error occurred while reading file.
     */
    protected String readFile(String filePath) throws IOException {
        InputStream inputStream = null;
        org.springframework.core.io.Resource resource;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            resource = new ClassPathResource(filePath);
            inputStream = resource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    /**
     * This method will assert the response of api result.
     * @param expectedFilePath - The expected api response file path.
     * @param actualAPIResult - The actual api result.
     * @throws Exception if any error occurred.
     */
    protected void assertResponse(String expectedFilePath, ResultActions actualAPIResult) throws Exception {
        String actualResult = actualAPIResult.andReturn().getResponse().getContentAsString();
        JSONObject resultObj = new JSONObject(actualResult);
        JSONObject expectedResultObj = new JSONObject(readFile(expectedFilePath));

        JSONObject content = resultObj.getJSONObject("result");
        String actualR = content.toString();
        JSONObject expectedContent = expectedResultObj.getJSONObject("result");
        String expectedR = expectedContent.toString();
        JSONAssert.assertEquals(expectedR, actualR, false);
    }

    /**
     * Check if the given string s is not an empty string nor null object..
     * @param s the given string.
     * @return true if the string has content, otherwise false.
     */
    protected Boolean isNotNullNorEmpty(String s) {
        return s != null && s.trim().length() != 0;
    }

    /**
     * <p>This method will load data from a given sql file.</p>
     * <p>The given sql file have to follow these roles.</p>
     * <ol>
     *     <li>The sql statement has remain in one single line. For example there can't be
     *     "update table1 \n set abc = null". That sql statement will cause an exception in this method.</li>
     *     <li>The file better too be very big. Over 500 lines for example. Otherwise use this method is useless.</li>
     * </ol>
     * @param fileName - The file name.
     * @param jdbcTemplate - The jdbc template used to execute the sql.
     * @throws IOException - if any error occurred while reading the file.
     */
    protected void loadDataFromFile(String fileName, NamedParameterJdbcTemplate jdbcTemplate) throws IOException {
        InputStream inputStream = null;
        org.springframework.core.io.Resource resource;
        BufferedReader bufferedReader = null;
        Map<String, Object> sqlParameters = new HashMap<String, Object>();
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        Integer count = 0;
        try {
            resource = new ClassPathResource(fileName);
            inputStream = resource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                // store one line data.
                stringBuilder.append("\n").append(line);
                count++;
                if (count == 100) {
                    // Execute the query every 50 times.
                    jdbcTemplate.update(stringBuilder.toString(), sqlParameters);
                    // reset the count and string builder.
                    count = 0;
                    stringBuilder = new StringBuilder();
                }
            }
            if (count > 0) {
                // If there are some query left in the end of file. Execute them.
                jdbcTemplate.update(stringBuilder.toString(), sqlParameters);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    /**
     * URL encodes the given parameter value.
     * @param string the value to encode
     * @return the encoded value
     * @throws UnsupportedEncodingException if UTF-8 is not supported
     * @since 1.1
     */
    protected String encode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }

    /**
     * Gets the value of the field <code>noProjectMember</code>.
     * @return the noProjectMember
     */
    protected String getNoProjectMember() {
        return noProjectMember;
    }
}
