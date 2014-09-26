/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.service.impl;

import com.topcoder.direct.dao.UserDAO;
import com.topcoder.direct.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.topcoder.direct.util.Helper.checkArgument;
import static com.topcoder.direct.util.Helper.isNotNullNorEmpty;

/**
 * <p>
 *     This service will used to get useful information for topcoder user.
 * </p>
 *
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * The user dao that used to retrieve data from database.
     */
    @Autowired
    private UserDAO userDAO;

    /**
     * Get user handle by given user id.
     * @param userId - The user id in topcoder system.
     * @return The user handle that map to given user id.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public String getUserHandle(Integer userId) throws IOException {
        checkArgument(userId > 0, "The user id should be positive.");
        return userDAO.getUserHandle(userId);
    }

    /**
     * Get the user id by social login
     * @param providerId - The social login provider id.
     * @param socialUserId - The social login user id. The user id from social web site.
     * @return The user id in topcoder system.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Integer getUserIdBySocialLogin(String providerId, String socialUserId) throws IOException {
        checkArgument(isNotNullNorEmpty(providerId), "provider id should be positive.");
        checkArgument(isNotNullNorEmpty(socialUserId), "social user id should be positive.");
        return userDAO.getUserIdBySocialLogin(providerId, socialUserId);
    }

    /**
     * Check if the given user id is a admin in topcoder system.
     * @param userId - The user id.
     * @return true if the user is admin otherwise false.
     * @throws IOException if error occurred while read query.
     */
    @Override
    public Boolean isAdmin(Integer userId) throws IOException {
        checkArgument(userId > 0, "The user id should be positive.");
        return userDAO.isAdmin(userId);
    }

}
