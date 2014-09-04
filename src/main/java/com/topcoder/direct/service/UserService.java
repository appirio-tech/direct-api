/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.service;

import java.io.IOException;

/**
 * <p>
 *     This service interface will define the method that can be used for managing topcoder user.
 * </p>
 *
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public interface UserService {
    /**
     * Get user handle by given user id.
     * @param userId - The user id in topcoder system.
     * @return The user handle that map to given user id.
     * @throws IOException if error occurred while read query.
     */
    String getUserHandle(Integer userId) throws IOException;

    /**
     * Get the user id by social login
     * @param providerId - The social login provider id.
     * @param socialUserId - The social login user id. The user id from social web site.
     * @return The user id in topcoder system.
     * @throws IOException if error occurred while read query.
     */
    Integer getUserIdBySocialLogin(String providerId, Integer socialUserId) throws IOException;

    /**
     * Check if the given user id is a admin in topcoder system.
     * @param userId - The user id.
     * @return true if the user is admin otherwise false.
     * @throws IOException if error occurred while read query.
     */
    Boolean isAdmin(Integer userId) throws IOException;

}
