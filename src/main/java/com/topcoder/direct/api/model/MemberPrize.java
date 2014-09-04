/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.model;

import java.util.List;

/**
 * Represents member prizes for a challenge.
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class MemberPrize {

    /**
     * List of winning prizes.
     */
    private List<Prize> prizes;

    /**
     * List of checkpoint prizes.
     */
    private List<Prize> checkPointPrizes;

    /**
     * Total prize.
     */
    private Double totalPrize;

    /**
     * Digital Run points.
     */
    private Double drPoints;

    /**
     * Reliability bonus.
     */
    private Double reliabilityBonus;

    /**
     * Empty constructor.
     */
    public MemberPrize() {
    }

    /**
     * Gets the value of the field <code>prizes</code>.
     *
     * @return the prizes
     */
    public List<Prize> getPrizes() {
        return prizes;
    }

    /**
     * Sets the value of the field <code>prizes</code>.
     *
     * @param prizes the prizes to set
     */
    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
    }

    /**
     * Gets the value of the field <code>checkPointPrizes</code>.
     *
     * @return the checkPointPrizes
     */
    public List<Prize> getCheckPointPrizes() {
        return checkPointPrizes;
    }

    /**
     * Sets the value of the field <code>checkPointPrizes</code>.
     *
     * @param checkPointPrizes the checkPointPrizes to set
     */
    public void setCheckPointPrizes(List<Prize> checkPointPrizes) {
        this.checkPointPrizes = checkPointPrizes;
    }

    /**
     * Gets the value of the field <code>totalPrize</code>.
     *
     * @return the totalPrize
     */
    public Double getTotalPrize() {
        return totalPrize;
    }

    /**
     * Sets the value of the field <code>totalPrize</code>.
     *
     * @param totalPrize the totalPrize to set
     */
    public void setTotalPrize(Double totalPrize) {
        this.totalPrize = totalPrize;
    }

    /**
     * Gets the value of the field <code>drPoints</code>.
     *
     * @return the drPoints
     */
    public Double getDrPoints() {
        return drPoints;
    }

    /**
     * Sets the value of the field <code>drPoints</code>.
     *
     * @param drPoints the drPoints to set
     */
    public void setDrPoints(Double drPoints) {
        this.drPoints = drPoints;
    }

    /**
     * Gets the value of the field <code>reliabilityBonus</code>.
     *
     * @return the reliabilityBonus
     */
    public Double getReliabilityBonus() {
        return reliabilityBonus;
    }

    /**
     * Sets the value of the field <code>reliabilityBonus</code>.
     *
     * @param reliabilityBonus the reliabilityBonus to set
     */
    public void setReliabilityBonus(Double reliabilityBonus) {
        this.reliabilityBonus = reliabilityBonus;
    }
}
