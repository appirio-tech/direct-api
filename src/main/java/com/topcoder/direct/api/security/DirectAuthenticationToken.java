/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.security;

import com.topcoder.direct.exception.UnauthorizedException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * <p>
 * This class will store the user authentication information.
 * </p>
 *
 * @author Ghost_141
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public class DirectAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * The user id.
     */
    private final Integer userId;

    /**
     * The user handle.
     */
    private final String handle;

    /**
     * The access level.
     */
    private final AccessLevel accessLevel;

    /**
     * Instantiates a new Direct authentication token.
     *
     * @param userId the user id
     * @param handle the handle
     * @param accessLevel the access level
     */
    DirectAuthenticationToken(Integer userId, String handle, AccessLevel accessLevel) {
        super(accessLevel.value().equals(AccessLevel.ANONYMOUS.value()) ? AuthorityUtils
            .createAuthorityList("ROLE_ANON")
            : (accessLevel.value().equals(AccessLevel.MEMBER.value()) ? AuthorityUtils
                .createAuthorityList("ROLE_MEMBER") : AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
        this.userId = userId;
        this.handle  = handle;
        this.accessLevel = accessLevel;
        setAuthenticated(accessLevel != AccessLevel.ANONYMOUS);
    }

    /**
     * Get credentials.
     *
     * @return null
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Get the principal.
     *
     * @return the user id.
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Gets handle.
     *
     * @return the handle
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets access level.
     *
     * @return the access level
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Restricts the operation to the given levels.
     *
     * @param accessLevels the levels that are allowed to continue processing
     * @throws UnauthorizedException if the current token is not one of the provided levels
     * @since 1.1
     */
    public void authorize(AccessLevel... accessLevels) {
        boolean allowed = false;
        for (AccessLevel access : accessLevels) {
            if (accessLevel == access) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new UnauthorizedException("Access Restricted.");
        }
    }
}
