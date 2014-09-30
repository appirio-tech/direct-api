/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.rowmapper;

import org.springframework.jdbc.core.RowMapper;

import com.appirio.tech.core.api.v2.CMCID;
import com.topcoder.direct.api.model.Challenge;
import com.topcoder.direct.api.model.MemberPrize;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.topcoder.direct.util.Helper.formatDate;
import static com.topcoder.direct.util.Helper.isNotNullNorEmpty;

/**
 * <p>
 *     This row mapper will map the query result set to a challenge object.
 * </p>
 *
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public class ChallengeRowMapper implements RowMapper<Challenge> {

    /**
     * The my challenge api date format.
     */
    private static final String MY_CHALLENGE_DATE_FORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Map the row from database to {@link Challenge} object instance.
     * @param rs - The row set.
     * @param rowNum - The current row number.
     * @return The challenge Instance.
     * @throws SQLException if any error occurred.
     */
    @Override
    public Challenge mapRow(ResultSet rs, int rowNum) throws SQLException {
        Challenge challenge = new Challenge();
        String technologies = rs.getString("challenge_technologies");
        String platforms = rs.getString("challenge_platforms");

        challenge.setId(new CMCID(rs.getInt("challenge_id")));
        challenge.setChallengeName(rs.getString("challenge_name"));
        challenge.setDrPoints(rs.getDouble("dr_points"));
        challenge.setChallengeType(rs.getString("challenge_type"));
        challenge.setBillingId(rs.getInt("billing_id"));
        challenge.setBillingName(rs.getString("billing_name"));
        challenge.setClientId(rs.getInt("client_id"));
        challenge.setClientName(rs.getString("client_name"));
        Integer directProjectId = rs.getInt("direct_project_id");
        challenge.setDirectProjectId(rs.wasNull() ? null : directProjectId);
        challenge.setDirectProjectName(rs.getString("direct_project_name"));
        challenge.setReliabilityBonus(rs.getDouble("reliability_bonus"));

        List<String> challengeTechnologies = new ArrayList<String>();
        if (isNotNullNorEmpty(technologies)) {
            List<String> res = Arrays.asList(technologies.split(","));
            for (String tech : res) {
                challengeTechnologies.add(tech.trim());
            }
        }
        challenge.setChallengeTechnologies(challengeTechnologies);

        List<String> challengePlatforms = new ArrayList<String>();
        if (isNotNullNorEmpty(platforms)) {
            List<String> res = Arrays.asList(platforms.split(","));
            for (String plat : res) {
                challengePlatforms.add(plat.trim());
            }
        }
        challenge.setChallengePlatforms(challengePlatforms);
        challenge.setChallengeStartDate(formatDate(rs.getDate("challenge_start_date"),
                MY_CHALLENGE_DATE_FORMAT));
        challenge.setChallengeEndDate(formatDate(rs.getDate("challenge_end_date"), MY_CHALLENGE_DATE_FORMAT));
        challenge.setChallengeStatus(rs.getString("challenge_status"));
        challenge.setChallengeCreator(rs.getString("challenge_creator"));

        return challenge;
    }
}
