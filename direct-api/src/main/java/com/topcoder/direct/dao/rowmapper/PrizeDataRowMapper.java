/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.rowmapper;

import org.springframework.jdbc.core.RowMapper;

import com.appirio.tech.core.api.v2.CMCID;
import com.topcoder.direct.api.model.Prize;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 *     This row mapper will map query result set to a prize object.
 * </p>
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public class PrizeDataRowMapper implements RowMapper<Prize> {
    /**
     * Map the row from database to {@link Prize} object instance.
     * @param rs - The row set.
     * @param rowNum - The current row number.
     * @return The challenge Instance.
     * @throws SQLException if any error occurred.
     */
    @Override
    public Prize mapRow(ResultSet rs, int rowNum) throws SQLException {
        Prize prz = new Prize();
        prz.setPlacement(rs.getInt("placement"));
        prz.setNumberOfPrize(rs.getInt("number_of_prize"));
        prz.setPrizeAmount(rs.getDouble("prize_amount"));
        prz.setChallengeId(rs.getInt("challenge_id"));
        prz.setPrizeType(rs.getInt("prize_type"));
        prz.setId(new CMCID(rs.getInt("prize_id")));
        return prz;
    }
}
