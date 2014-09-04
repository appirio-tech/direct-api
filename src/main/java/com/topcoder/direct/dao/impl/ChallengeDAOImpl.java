/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.impl;

import com.topcoder.direct.api.model.Challenge;
import com.topcoder.direct.api.model.Prize;
import com.topcoder.direct.dao.ChallengeDAO;
import com.topcoder.direct.dao.rowmapper.ChallengeRowMapper;
import com.topcoder.direct.dao.rowmapper.PrizeDataRowMapper;
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
 * The challenge DAO that will retrieve challenge data from database. This class is a jdbc implementation of
 * ChallengeDAO interface.
 * </p>
 *
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Repository
public class ChallengeDAOImpl implements ChallengeDAO {
    /**
     * The jdbc template that for tcs_catalog database.
     */
    @Autowired
    @Qualifier("tcsCatalogJdbc")
    private NamedParameterJdbcTemplate tcsCatalogJdbcTemplate;

    /**
     * Get my challenges from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @param orderBy - the order clause
     * @return A list of challenges.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public List<Challenge> getMyChallenge(List<String> customFilter, Map<String, Object> sqlParameters, String orderBy)
        throws IOException {
        String query = readQuery("get_my_challenges");
        if (orderBy != null) {
            query += " " + orderBy;
        }
        query = addFilterToQuery(query, customFilter);
        return DataAccess.executeSqlQuery(query, sqlParameters, tcsCatalogJdbcTemplate, new ChallengeRowMapper());
    }

    /**
     * Get my challenges prizes data from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return a list of member prize instance.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public List<Prize> getMyChallengesPrizes(List<String> customFilter, Map<String, Object> sqlParameters)
        throws IOException {
        String query = addFilterToQuery(readQuery("get_my_challenges_prizes"), customFilter);
        return DataAccess.executeSqlQuery(query, sqlParameters, tcsCatalogJdbcTemplate, new PrizeDataRowMapper());
    }

    /**
     * Get my challenge count info from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return the total count of my challenges.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Integer getMyChallengesCount(List<String> customFilter, Map<String, Object> sqlParameters)
        throws IOException {
        return DataAccess.getSingleResultBySqlQuery(
            addFilterToQuery(readQuery("get_my_challenges_count"), customFilter), sqlParameters, Integer.class,
            tcsCatalogJdbcTemplate).get(0);
    }

    /**
     * Add filter into query manually.
     * @param query The query content.
     * @param filterToAdd The filter that need to add into query.
     * @return the combined query
     */
    private String addFilterToQuery(String query, List<String> filterToAdd) {
        StringBuilder stringBuilder = new StringBuilder();
        Integer splitPoint = query.toLowerCase().lastIndexOf("order by");
        if (splitPoint == -1) {
            // The query don't have order by clause
            stringBuilder.append(query);
        } else {
            stringBuilder.append(query.substring(0, splitPoint));
        }
        // Add filter.
        for (String f : filterToAdd) {
            stringBuilder.append(f);
        }
        // Only add what left in query if there is an order by clause in it.
        if (splitPoint != -1) {
            stringBuilder.append(query.substring(splitPoint, query.length()));
        }
        return stringBuilder.toString();
    }
}
