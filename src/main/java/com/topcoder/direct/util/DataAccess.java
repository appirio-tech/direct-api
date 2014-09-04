/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.util;

import com.topcoder.direct.exception.BadRequestException;
import com.topcoder.direct.exception.ServerInternalException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static com.topcoder.direct.util.Helper.logException;

/**
 * <p>
 *     This class provide several helpful methods for reading query, executing query and get result from database.
 * </p>
 *
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Component
public class DataAccess {

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(DataAccess.class);

    /**
     * The query folder.
     * The value will be injected by spring.
     */
    private static String QUERY_FOLDER;

    /**
     * This method will execute the query by given query name and the
     * @param queryName The query name.
     * @param sqlParameters the sql parameters.
     * @param jdbcTemplate the named parameter jdbc template used to execute the query.
     * @param rowMapper - The row mapper.
     * @return the execution results.
     * @throws BadRequestException if any error occurred.
     */
    public static <T> List<T> executeQuery(String queryName, Map<String, ?> sqlParameters,
            NamedParameterJdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) throws IOException {
        logger.debug("Entrance method executeQuery");
        logger.debug(String.format("Executing query: %s with parameters: %s", queryName, sqlParameters));
        List<T> res = executeSqlQuery(readQuery(queryName), sqlParameters, jdbcTemplate, rowMapper);
        logger.debug("Exit method executeQuery: " + res);
        return res;
    }

    /**
     * This method will execute the sql query directly.
     * @param sql - The sql content.
     * @param sqlParameters - The sql parameters.
     * @param jdbcTemplate - The named parameter jdbc template used to execute query.
     * @param rowMapper - The row mapper.
     * @return The execution result.
     */
    public static <T> List<T> executeSqlQuery(String sql, Map<String, ?> sqlParameters,
            NamedParameterJdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        try {
            logger.debug("Entrance method executeSqlQuery: [sql: ");
            logger.debug(String.format("Executing sql: %s with parameters: %s", sql, sqlParameters));
            List<T> res = jdbcTemplate.query(sql, sqlParameters, rowMapper);
            logger.debug("Exit method executeSqlQuery");
            return res;
        } catch (DataAccessException dae) {
            throw logException(logger, "executeQuery()",
                    new ServerInternalException(String.format("The query:\n %s \n is failed to execute.", sql), dae));
        }
    }

    /**
     * This method will use to read query content from the query file.
     * @param file The query file name.
     * @return the query content.
     * @throws IOException if error occurred while read query.
     */
    public static String readQuery(String file) throws IOException {
        InputStream inputStream = null;
        Resource resource;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            logger.debug("Entrance method readQuery");
            resource = new ClassPathResource(QUERY_FOLDER + File.separator + file);
            inputStream = resource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            logger.debug("Exit method readQuery");
            return stringBuilder.toString();
        } catch (IOException ioe) {
            throw logException(logger, "executeQuery()",
                    new ServerInternalException(String.format("The query %s is failed to read", file), ioe));
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
     * Get single result from database by given query name and result type.
     * @param queryName The query name.
     * @param sqlParameters The sql parameters.
     * @param elementType The result type.
     * @param jdbcTemplate The jdbc template used to execute query.
     * @return A list of target element.
     * @throws IOException if error occurred while read query.
     */
    public static <T> List<T> getSingleResultByQuery(String queryName, Map<String, ?> sqlParameters,
            Class<T> elementType, NamedParameterJdbcTemplate jdbcTemplate) throws IOException {
        return getSingleResultBySqlQuery(readQuery(queryName), sqlParameters, elementType, jdbcTemplate);
    }

    /**
     * Get single result from database by given query content and result type.
     * @param queryContent The query content.
     * @param sqlParameters The sql parameters.
     * @param elementType The result type.
     * @param jdbcTemplate The jdbc template used to execute query.
     * @return A list of target element.
     */
    public static <T> List<T> getSingleResultBySqlQuery(String queryContent, Map<String, ?> sqlParameters,
            Class<T> elementType, NamedParameterJdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(queryContent, sqlParameters, elementType);
    }

    /**
     * Setter for QUERY_FOLDER field.
     * @param QUERY_FOLDER the inject value from spring.
     */
    @Value("${query_folder}")
    public void setQUERY_FOLDER(String QUERY_FOLDER) {
        DataAccess.QUERY_FOLDER = QUERY_FOLDER;
    }

    /**
     * constructor.
     */
    public DataAccess() {
    }
}
