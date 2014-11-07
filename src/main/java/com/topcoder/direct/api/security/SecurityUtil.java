/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.security;

import static com.topcoder.direct.util.Helper.checkArgument;
import static com.topcoder.direct.util.Helper.isNull;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTVerifier;
import com.topcoder.direct.exception.BadRequestException;
import com.topcoder.direct.exception.ServerInternalException;
import com.topcoder.direct.service.UserService;

/**
 * Service for security related functions.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 * @since 1.0 (Topcoder Direct API - My Challenges API v1.0)
 */
@Component("directSecurityService")
public class SecurityUtil {

    /**
     * The value of topcoder ad.
     */
    private static final String TOPCODER_AD = "ad";

    /**
     * The token pattern.
     */
    private static final String TOKEN_PATTERN = "^[bB]earer\\s.+";

    /**
     * The pattern used to split the token.
     */
    private static final String TOKEN_SPLIT_PATTERN = "^[bB]earer\\s";

    /**
     * Static singleton instantiated by spring (context aware).
     */
    private static SecurityUtil instance;

    /**
     * The secret of auth0 account.
     */
    @Value("${jwt.oauthClientSecret}")
    private String oauthClientSecret;

    /**
     * The client id of auth0 account.
     */
    @Value("${jwt.oauthClientId}")
    private String oauthClientId;

    /**
     * The user service.
     */
    @Autowired
    private UserService userService;

    /**
     * Package visible constructor.
     */
    SecurityUtil() {
    }

    /**
     * Initializes the static instance.
     */
    @PostConstruct
    void init() {
        instance = this;
    }

    /**
     * Retrieves the authentication information from the given request.
     *
     * @param request - The http request.
     * @return the authentication object containing the user information
     * @throws BadRequestException - for invalid requests.
     * @throws ServerInternalException - for other processing errors.
     */
    public static final DirectAuthenticationToken getAuthentication(HttpServletRequest request) {
        return instance.getToken(request);
    }

    /**
     * Retrieves the authentication information from the given request.
     *
     * @param request - The http request.
     * @return the authentication object containing the user information
     * @throws BadRequestException - for invalid requests.
     * @throws ServerInternalException - for other processing errors.
     */
    private DirectAuthenticationToken getToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            // Initial a anonymous caller and set it into request.
            Integer userId = 0;

            if (token == null) {
                // The anonymous caller.
                DirectAuthenticationToken authentication =
                    new DirectAuthenticationToken(0, null, AccessLevel.ANONYMOUS);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                return authentication;
            }

            checkArgument(token.matches(TOKEN_PATTERN), "Malformed Auth header");

            Map<String, Object> decodedPayload = new JWTVerifier(oauthClientSecret, oauthClientId).verify(token
                .split(TOKEN_SPLIT_PATTERN)[1]);
            checkArgument(!isNull(decodedPayload.get("sub")), "Malformed Auth header. No sub in token!");

            String userData = decodedPayload.get("sub").toString();
            String[] splitResults = userData.split("\\|");

            checkArgument(splitResults.length >= 2, "Malformed Auth header. userId or provider is missing.");

            String socialUserId = splitResults[1];
            if (splitResults.length==3) {
                socialUserId = splitResults[2];
            }
            String socialUserProvider = splitResults[0];

            //userId = socialUserId;

            // Fetch the userId for social login user.
            if (!socialUserProvider.equals(TOPCODER_AD)) {
                userId = userService.getUserIdBySocialLogin(socialUserProvider, socialUserId);
            } else {
                userId = Integer.valueOf(socialUserId);
            }

            String handle = userService.getUserHandle(userId);
            AccessLevel accessLevel = userService.isAdmin(userId) ? AccessLevel.ADMIN : AccessLevel.MEMBER;

            DirectAuthenticationToken authentication = new DirectAuthenticationToken(userId, handle, accessLevel);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
            return authentication;

        } catch (NumberFormatException nfe) {
            throw new BadRequestException("Invalid user id.");
        } catch (IllegalStateException e) {
            throw new BadRequestException("JWT Expired.");
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException(iae.getMessage(), iae);
        } catch (NoSuchAlgorithmException e) {
            throw new ServerInternalException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new BadRequestException("Invalid Key.", e);
        } catch (SignatureException e) {
            throw new BadRequestException("Invalid Signature.", e);
        } catch (IOException e) {
            throw new ServerInternalException("Unable to complete operation.", e);
        }
    }
}
