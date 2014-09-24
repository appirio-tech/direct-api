/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao.impl;

import com.topcoder.direct.dao.UserDAO;
import com.topcoder.direct.exception.BadRequestException;
import com.topcoder.direct.util.DataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topcoder.direct.util.Helper.isNull;

/**
 * <p>
 *     The user DAO that used to retrieve data from database. This class is a jdbc implementation of UserDAO interface.
 * </p>
 *
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Repository
public class UserDAOImpl implements UserDAO {

    /**
     * The injected jdbc template for database common_oltp.
     */
    @Autowired
    @Qualifier("commonOltpJdbc")
    private NamedParameterJdbcTemplate commonOltpJdbcTemplate;

    /**
     * Logger instance.
     */
    private static final Logger LOG = Logger.getLogger(UserDAOImpl.class);

    /**
     * Gets user handle by given user id.
     *
     * @param userId the user id
     * @return If the user id exist, return user handle otherwise null.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public String getUserHandle(Integer userId) throws IOException {

        List<String> userHandle = DataAccess.getSingleResultByQuery("get_user_handle",
                Collections.singletonMap("user_id", userId), String.class, commonOltpJdbcTemplate);

        if (isNull(userHandle) || userHandle.isEmpty()) {
            throw new BadRequestException(String.format("The userId: %s is not exist.", userId));
        }
        return userHandle.get(0);
    }

    /**
     * Get user id by given social login information.
     * @param providerId - The social login provider id.
     * @param socialUserId - The social login user id. The user id in social web site.
     * @return The user id in topcoder system.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Integer getUserIdBySocialLogin(String providerId, Integer socialUserId) throws IOException {
        Map<String, Object> sqlParameters = new HashMap<String, Object>();

        int pid = 0;
        if (providerId.toLowerCase().startsWith("facebook")) {
            pid = 1;
        } else if (providerId.toLowerCase().startsWith("google")) {
            pid = 2;
        } else if (providerId.toLowerCase().startsWith("twitter")) {
            pid = 3;
        } else if (providerId.toLowerCase().startsWith("github")) {
            pid = 4;
        } 

        sqlParameters.put("provider_id", pid);
        sqlParameters.put("social_user_id", socialUserId);
        List<Integer> userId = DataAccess.getSingleResultByQuery("get_user_by_social_login", sqlParameters,
                Integer.class, commonOltpJdbcTemplate);
        if (isNull(userId) || userId.isEmpty()) {
            throw new BadRequestException("The social login not found.");
        }
        return userId.get(0);
    }

    /**
     * Check if the given user id is represent a admin in topcoder system.
     * @param userId - The user id.
     * @return true if the user is admin, otherwise false.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Boolean isAdmin(Integer userId) throws IOException {
        return DataAccess.getSingleResultByQuery("check_is_admin", Collections.singletonMap("user_id", userId),
                Integer.class, commonOltpJdbcTemplate).get(0) == 1;
    }

}
