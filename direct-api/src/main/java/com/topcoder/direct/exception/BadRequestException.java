/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.exception;

import com.appirio.tech.core.api.v2.exception.CMCRuntimeException;

/**
 * The bad request exception for rest api.
 *
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public class BadRequestException extends CMCRuntimeException {
    /**
     * The message of exception.
     */
    private String message;

    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the error message
     */
    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the error message
     * @param throwable the cause of this error
     */
    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    /**
     * Get message.
     * @return The message.
     */
    public String getMessage() {
        return this.message;
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
