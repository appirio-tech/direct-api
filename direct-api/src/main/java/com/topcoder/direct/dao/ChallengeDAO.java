/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.topcoder.direct.api.model.Challenge;
import com.topcoder.direct.api.model.Prize;

/**
 * <p>
 * The challenge DAO interface defines all the method that get or create challenge information in database.
 * </p>
 *
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public interface ChallengeDAO {
    /**
     * Get my challenges from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @param orderBy - the order clause
     * @return A list of challenges.
     * @throws IOException if error occurred while read query.
     */
    List<Challenge> getMyChallenge(List<String> customFilter, Map<String, Object> sqlParameters, String orderBy)
        throws IOException;

    /**
     * Get my challenges prizes data from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return a list of member prize instance.
     * @throws IOException if error occurred while read query.
     */
    List<Prize> getMyChallengesPrizes(List<String> customFilter, Map<String, Object> sqlParameters) throws IOException;

    /**
     * Get my challenge count info from database.
     *
     * @param customFilter - A list of custom filter that need to add into query.
     * @param sqlParameters - The sql parameters
     * @return the total count of my challenges.
     * @throws IOException if error occurred while read query.
     */
    Integer getMyChallengesCount(List<String> customFilter, Map<String, Object> sqlParameters) throws IOException;
}
