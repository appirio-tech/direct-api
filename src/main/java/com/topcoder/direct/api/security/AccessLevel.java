/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.security;

/**
 * The AccessLevel enum. This enum will state the access level of a caller.
 * @author Ghost_141
 * @version 1.0
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public enum AccessLevel {

    /**
     * The ANONYMOUS access level.
     */
    ANONYMOUS(0, "anonymous"),

    /**
     * The MEMBER access level.
     */
    MEMBER(1, "member"),

    /**
     * The ADMIN access level.
     */
    ADMIN(2, "admin");

    /**
     * The value of this instance.
     */
    private Integer value;

    /**
     * The level description of this instance.
     */
    private String level;

    /**
     * Private constructor.
     * @param value the value.
     * @param level the level description.
     */
    private AccessLevel(Integer value, String level) {
        this.value = value;
        this.level = level;
    }

    /**
     * Get the value of this instance.
     *
     * @return the integer
     */
    public Integer value() {
        return this.value;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public String getLevel() {
        return level;
    }
}
