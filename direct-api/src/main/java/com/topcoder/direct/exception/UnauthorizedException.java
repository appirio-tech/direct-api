/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.exception;

import com.appirio.tech.core.api.v2.exception.CMCRuntimeException;

/**
 * The unauthorized exception for rest controller.
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public class UnauthorizedException extends CMCRuntimeException {
    /**
     * The message of exception.
     */
    private String message;

    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the error message
     */
    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the error message
     * @param throwable the cause of this error
     */
    public UnauthorizedException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    /**
     * Get the message.
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
