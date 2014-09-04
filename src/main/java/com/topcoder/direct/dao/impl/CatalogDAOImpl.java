/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.impl;

import com.topcoder.direct.dao.CatalogDAO;
import com.topcoder.direct.util.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *     The catalog DAO. This DAO will retrieve some static value from tcs_catalog database.
 * </p>
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Repository
public class CatalogDAOImpl implements CatalogDAO {

    /**
     * The jdbc template used to execute query.
     */
    @Autowired
    @Qualifier("tcsCatalogJdbc")
    private NamedParameterJdbcTemplate tcsCatalogJdbcTemplate;

    /**
     * Get the a list of id from database tcs_catalog by given id name.
     * @param idName - The id name.
     * @param filterValues - The filter values
     * @param parameterName - The name for sql parameters.
     * @return a list of id from database tcs_catalog.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public List<Integer> getIds(String idName, List<String> filterValues, String parameterName) throws IOException {
        if (filterValues != null && !filterValues.isEmpty()) {
            List<String> filters = new ArrayList<String>();
            for (String value : filterValues) {
                filters.add(value.toLowerCase().trim());
            }
            return DataAccess.getSingleResultByQuery("get_" + idName + "_id",
                    Collections.singletonMap(parameterName, filters), Integer.class, tcsCatalogJdbcTemplate);
        } else {
            return DataAccess.getSingleResultByQuery("get_" + idName + "_id", null, Integer.class,
                    tcsCatalogJdbcTemplate);

        }
    }
}
