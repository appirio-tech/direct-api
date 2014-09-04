/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.model;

import org.springframework.stereotype.Component;

import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;

/**
 * Represents a challenge prize.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
@Component
public class Prize extends AbstractIdResource {

    /**
     * API Query resource path.
     */
    public static final String RESOURCE_PATH = "prizes";

    /**
     * Placement.
     */
    private Integer placement;

    /**
     * Number of prizes.
     */
    private Integer numberOfPrize;

    /**
     * Prize amount.
     */
    private Double prizeAmount;

    /**
     * Type.
     */
    private Integer prizeType;

    /**
     * Associated challenge.
     */
    private Integer challengeId;

    /**
     * Empty constructor.
     */
    public Prize() {
    }

    /**
     * Returns the resource path for this entity.
     *
     * @return {@value #RESOURCE_PATH}
     */
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * Gets the value of the field <code>placement</code>.
     *
     * @return the placement
     */
    public Integer getPlacement() {
        return placement;
    }

    /**
     * Gets the value of the field <code>numberOfPrize</code>.
     *
     * @return the numberOfPrize
     */
    public Integer getNumberOfPrize() {
        return numberOfPrize;
    }

    /**
     * Gets the value of the field <code>prizeAmount</code>.
     *
     * @return the prizeAmount
     */
    public Double getPrizeAmount() {
        return prizeAmount;
    }

    /**
     * Gets the value of the field <code>prizeType</code>.
     *
     * @return the prizeType
     */
    public Integer getPrizeType() {
        return prizeType;
    }

    /**
     * Gets the value of the field <code>challengeId</code>.
     *
     * @return the challengeId
     */
    public Integer getChallengeId() {
        return challengeId;
    }

    /**
     * Sets the value of the field <code>placement</code>.
     * @param placement the placement to set
     */
    public void setPlacement(Integer placement) {
        this.placement = placement;
    }

    /**
     * Sets the value of the field <code>numberOfPrize</code>.
     * @param numberOfPrize the numberOfPrize to set
     */
    public void setNumberOfPrize(Integer numberOfPrize) {
        this.numberOfPrize = numberOfPrize;
    }

    /**
     * Sets the value of the field <code>prizeAmount</code>.
     * @param prizeAmount the prizeAmount to set
     */
    public void setPrizeAmount(Double prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    /**
     * Sets the value of the field <code>prizeType</code>.
     * @param prizeType the prizeType to set
     */
    public void setPrizeType(Integer prizeType) {
        this.prizeType = prizeType;
    }

    /**
     * Sets the value of the field <code>challengeId</code>.
     * @param challengeId the challengeId to set
     */
    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }
}
