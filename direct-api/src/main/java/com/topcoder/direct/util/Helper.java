/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.topcoder.direct.exception.BadRequestException;

/**
 * <p>
 *     This class contains several helper method that allow coder build Direct API easier.
 * </p>
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
public final class Helper {

    /**
     * Private constructor.
     */
    private Helper() {
    }

    /**
     * Check if given object is part of the given allowed values.
     * @param allowedValues the allowed values.
     * @param object the object that need to check.
     * @param objectName the object name
     * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
     */
    public static <T> void checkContains(List<T> allowedValues, T object, String objectName) {
        checkArgument(allowedValues.contains(object), String.format("The %s should be an element of %s.", objectName,
                join(allowedValues, ", ")));
    }

    /**
     * Join the list with joiner.
     * @param list a list value that need to join.
     * @param joiner The joiner that used to join the list.
     * @return the joined string.
     * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
     */
    public static <T> String join(List<T> list, String joiner) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return "";
        }
        for (T s : list) {
            stringBuilder.append(s).append(joiner);
        }
        stringBuilder.delete(stringBuilder.length() - joiner.length(), stringBuilder.length());
        return stringBuilder.toString();
    }

    /**
     * Log the exception.
     * @param logger the log4j logger.
     * @param methodName the method name.
     * @param exception the exception to log.
     * @return The given exception.
     */
    public static <T extends Throwable> T logException(Logger logger, String methodName, T exception) {
        logger.error(String.format("Exception [%s] in method [%s], details: %s",
                exception.getClass().getSimpleName(), methodName, exception.getMessage()), exception);
        return exception;
    }

    /**
     * Check if the given object is null.
     * @param object the object.
     * @return true if the object is null, otherwise false.
     */
    public static Boolean isNull(Object object) {
        return object == null;
    }

    /**
     * Check if the given string s is not an empty string nor null object..
     * @param s the given string.
     * @return true if the string has content, otherwise false.
     */
    public static Boolean isNotNullNorEmpty(String s) {
        return s != null && s.trim().length() != 0;
    }

    /**
     * Format the date based given date format.
     * @param date The date value.
     * @param format The format string.
     * @return The format value.
     */
    public static String formatDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Check the given expression. If it's true then do nothing, otherwise throw an {@link IllegalArgumentException}
     * exception.
     * @param expression - The given expression.
     * @param errMsg The error message that used to construct IllegalArgumentException.
     */
    public static void checkArgument(Boolean expression, String errMsg) {
        if (!expression) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    /**
     * Per spec.
     *
     *  1) String value needs to be surrounded by single quotation(')
     *
     * This method will attempt to "unquote" the given value.
     * This is more tolerant that it would still consider the value as a valid string if the quotes are missing.
     * @param val the value to remove the quotes for
     *
     * @return the unquoted string
     * @throws BadRequestException if there is an improperly quoted string found
     * @since 1.1
     */
    public static String unQuoteString(String val) {
        val = val.trim(); // they have to put any spaces to be retained inside the quotes
        int index = 0;
        if (val.startsWith("'")) {
            index = scanTerminator(val, index);
            if (index != val.length()) {
                throw new BadRequestException("Unexpected end of string at " + index);
            }
            String qs = val.substring(1, val.length() - 1);
            return qs.replace("\\'", "'");
        } else {
            // unquoted string, return as is
            return val;
        }
    }

    /**
     * Scans for the terminating quote of the given value starting from the given position.
     * @param val the value to scan
     * @param index the current position
     * @return the matching single quote terminator
     * @throws BadRequestException if no terminator is found
     */
    private static int scanTerminator(String val, int index) {
        index += 1;
        boolean terminated = false;
        while (!terminated) {
            int offset = val.indexOf("'", index);
            if (offset == -1) {
                throw new BadRequestException("Unterminated string." + val);
            } else {
                if (val.charAt(offset - 1) == '\\') {
                    // skip to allow escape of single quote
                } else {
                    terminated = true;
                }
            }
            index = offset + 1;
        }
        return index;
    }

    /**
     * Attempts to parse the given string as a csv, with possible element quotations using '.
     * @param val the value to parse
     *
     * @return the parsed values
     * @throws BadRequestException if there is an improperly quoted string found
     * @since 1.1
     */
    public static String[] unQuoteAndSplit(String val) {
        List<String> tokens = new ArrayList<String>();
        while (val.length() != 0) {
            if (val.charAt(0) == '\'') { // quoted element, copy until the next terminator
                int terminator = scanTerminator(val, 0);
                tokens.add(val.substring(1, terminator - 1).replace("\\'", "'"));
                val = val.substring(terminator).trim();
                if (val.length() > 0) {
                    // if the quoted string terminated at this point, this should be a comma or eol
                    if (val.charAt(0) != ',') {
                        throw new BadRequestException("End of token expected but found " + val);
                    } else {
                        val = val.substring(1).trim(); // remove next ","
                    }
                }
            } else { // unquoted element, just copy until the next comma
                int terminator = val.indexOf(',');
                if (terminator == -1) { // no more tokens, copy until eol
                    tokens.add(val.trim());
                    val = "";
                } else {
                    tokens.add(val.substring(0, terminator).trim());
                    val = val.substring(terminator + 1).trim();
                }
            }
        }
        return tokens.toArray(new String[0]);
    }
}
