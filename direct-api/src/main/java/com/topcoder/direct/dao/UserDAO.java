/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.dao;

import java.io.IOException;

/**
 * <p>
 *     The user DAO interface defines all the method that associate with database to get or create information in db.
 * </p>
 *
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public interface UserDAO {
    /**
     * Gets user handle by given user id.
     *
     * @param userId the user id
     * @return If the user id exist, return user handle otherwise null.
     * @throws IOException if error occurred while read query.
     */
    String getUserHandle(Integer userId) throws IOException;

    /**
     * Get user id by given social login information.
     * @param providerId - The social login provider id.
     * @param socialUserId - The social login user id. The user id in social web site.
     * @return The user id in topcoder system.
     * @throws IOException if error occurred while read query.
     */
    Integer getUserIdBySocialLogin(String providerId, Integer socialUserId) throws IOException;

    /**
     * Check if the given user id is represent a admin in topcoder system.
     * @param userId - The user id.
     * @return true if the user is admin, otherwise false.
     * @throws IOException if error occurred while read query.
     */
    Boolean isAdmin(Integer userId) throws IOException;
}
