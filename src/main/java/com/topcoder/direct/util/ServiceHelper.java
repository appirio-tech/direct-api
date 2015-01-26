/*
 * Copyright (C) 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.util;

import com.appirio.tech.core.api.v2.request.LimitQuery;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The utility class provides helper methods for RESTQueryService implementations.
 *
 * @author TCSASSEMBLER
 * @version 1.0 (TopCoder Direct API - Project Retrieval API)
 */
public final class ServiceHelper {

    /**
     * Private constructor prevents instantiation.
     */
    private ServiceHelper() {
    }

    /**
     * Strips of "in()" from the given request parameter and parses it as a list. If it is not a list then a single
     * element will be used
     * <p/>
     * Per spec:
     * 2) filedValue(sic) can contain multiple values using in() format {fieldName}=in({fieldValue1},{fieldValue1})
     *
     * @param object         the parameter object
     * @param forceLowerCase converts the value to lower case
     * @param logger         the logger to log the parsed result
     * @return the list of parsed values
     */
    public static List<String> parseFilterValueToValueList(Object object, boolean forceLowerCase, Logger logger) {
        List<String> values = new ArrayList<String>();
        if (object != null) {
            String val = object.toString().trim();
            if (forceLowerCase) {
                val = val.toLowerCase();
            }

            String tmp = val.toLowerCase().replaceAll("\\s", ""); // to ignore any space between IN and (
            if (val.toLowerCase().startsWith("in") && tmp.startsWith("in(") && tmp.endsWith(")")) {
                // multiple value format!
                val = val.substring(val.indexOf("(") + 1, val.length() - 1);

                // each item can vary types, we can't just split it based on ',' because it might be a quoted string
                for (String string : Helper.unQuoteAndSplit(val)) {
                    values.add(string);
                }
            } else {
                // single value format
                values.add(Helper.unQuoteString(val));
            }
        }
        logger.debug("Parsed filter values: " + values);
        return values;
    }

    /**
     * Populates the SQL parameters for pagination. Uses LimitQuery.
     *
     * @param query         The query parameters of request.
     * @param sqlParameters Current SQL parameters.
     */
    public static void populateLimitQuery(QueryParameter query, Map<String, Object> sqlParameters) {
        LimitQuery limitQuery = query.getLimitQuery();
        int offset = 0;
        int pageSize = 10;

        if (limitQuery != null) {
            if (limitQuery.getLimit() != null) {
                pageSize = limitQuery.getLimit();
                if (pageSize == -1) {
                    pageSize = Integer.MAX_VALUE;
                }
            }
            if (limitQuery.getOffset() != null) {
                offset = limitQuery.getOffset();
            }
        }
        sqlParameters.put("page_size", pageSize);
        sqlParameters.put("first_row_index", offset);
    }
}
