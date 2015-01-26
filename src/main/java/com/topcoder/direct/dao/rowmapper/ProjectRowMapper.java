/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.rowmapper;

import com.appirio.tech.core.api.v2.CMCID;
import com.topcoder.direct.api.model.Project;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.topcoder.direct.util.Helper.formatDate;


/**
 * <p>
 * This row mapper will map the query result set to a project billing account object.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
public class ProjectRowMapper implements RowMapper<Project> {

    /**
     * The my project api date format.
     */
    private static final String MY_PROJECT_DATE_FORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Map the row from database to {@link com.topcoder.direct.api.model.Project} object instance.
     *
     * @param rs     - The row set.
     * @param rowNum - The current row number.
     * @return The Project Instance.
     * @throws SQLException if any error occurred.
     */
    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();

        project.setId(new CMCID(rs.getInt("project_id")));
        project.setProjectName(rs.getString("project_name"));
        project.setProjectStatusId(rs.getInt("project_status_id"));
        project.setProjectStatusName(rs.getString("project_status_name"));
        project.setProjectCreatedDate(formatDate(rs.getDate("project_created_date"), MY_PROJECT_DATE_FORMAT));
        project.setProjectCreatedBy(rs.getInt("project_creator_id"));
        project.setProjectLastUpdatedDate(formatDate(rs.getDate("project_updated_date"), MY_PROJECT_DATE_FORMAT));

        return project;
    }
}
