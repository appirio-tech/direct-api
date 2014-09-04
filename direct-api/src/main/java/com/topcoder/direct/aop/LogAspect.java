/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.aop;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The aspect class that used to log entrance and result of rest controller.
 * @author Ghost_141, TCSASSEMBLER
 * @version 1.1
 * @since 1.0 (TopCoder Direct API Setup and implement My Created Challenges API)
 */
@Component
@Aspect
public class LogAspect {

    /**
     * This method will use to log entrance and result of public method in com.topcoder.direct package.
     * @param pjp The proceed join point.
     * @return The output of direct rest service.
     * @throws Throwable If any error occurred.
     */
    @Around("execution(public * com.topcoder.direct..*(..)))")
    public Object logController(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        Logger logger = Logger.getLogger(signature.getDeclaringType());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Log the entrance.
        logger.info(String.format("Entrance method %s: %s", signature.getName(),
                Arrays.toString(pjp.getArgs())));
        Object returnVal = pjp.proceed();
        // Log the results.
        logger.info(String.format("Exit method %s: %s", signature.getName(),
            objectMapper.writeValueAsString(returnVal)));
        return returnVal;
    }

    /**
     * This aspect logs all exceptions that bubble up to the service layer, before the framework
     * handles it and converts the exception to a user message.
     *
     * By using only the service layer as point cut, we make sure direct.api logs the error only once
     * @param joinPoint the exception join point.
     * @param error the exception thrown
     * @since 1.1
     */
    @AfterThrowing(pointcut = "execution(* com.topcoder.direct.api.services.*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        Signature signature = joinPoint.getSignature();
        Logger logger = Logger.getLogger(signature.getDeclaringType());
        logger.error("Encountered an exception in " + signature.getName(), error);
    }

}
