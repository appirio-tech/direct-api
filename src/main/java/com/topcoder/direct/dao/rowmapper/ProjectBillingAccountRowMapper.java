/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.rowmapper;

import com.appirio.tech.core.api.v2.CMCID;
import com.topcoder.direct.api.model.ProjectBillingAccount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 *     This row mapper will map the query result set to a project billing account object.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
public class ProjectBillingAccountRowMapper implements RowMapper<ProjectBillingAccount> {

    /**
     * Map the row from database to {@link com.topcoder.direct.api.model.ProjectBillingAccount} object instance.
     * @param rs - The row set.
     * @param rowNum - The current row number.
     * @return The ProjectBillingAccount Instance.
     * @throws SQLException if any error occurred.
     */
    @Override
    public ProjectBillingAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectBillingAccount billing = new ProjectBillingAccount();

        billing.setId(new CMCID(rs.getInt("billing_id")));
        billing.setProjectId(rs.getInt("project_id"));
        billing.setName(rs.getString("billing_name"));

        return billing;
    }
}
