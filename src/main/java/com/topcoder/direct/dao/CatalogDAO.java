/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *     This dao interface will define the method that associate with database to get catalog information.
 * </p>
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public interface CatalogDAO {
    /**
     * Get the a list of id from database tcs_catalog by given id name.
     * @param idName - The id name.
     * @param filterValues - The filter values
     * @param parameterName - The name for sql parameters.
     * @return a list of id from database tcs_catalog.
     * @throws IOException if error occurred while read query.
     */
    List<Integer> getIds(String idName, List<String> filterValues, String parameterName) throws IOException;
}
