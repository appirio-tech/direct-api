/*
 * Copyright (C) 2014 - 2015 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.model;

import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Represents the challenge model.
 *
 * <p>
 * Version 1.1 (TopCoder Direct API - Project Retrieval API)
 * - Add @JsonIgnore to hide {@link #getResourcePath()} from json result.
 * </p>
 *
 * @author j3_guile, GreatKevin
 * @version 1.1
 */
@Component
public class Challenge extends AbstractIdResource {

    /**
     * Query API resource mapping.
     */
    public static final String RESOURCE_PATH = "challenges";

    /**
     * Challenge name.
     */
    private String challengeName;

    /**
     * Challenge type.
     */
    private String challengeType;

    /**
     * Client name.
     */
    private String clientName;

    /**
     * Client id.
     */
    private Integer clientId;

    /**
     * Billing name.
     */
    private String billingName;

    /**
     * Billing id.
     */
    private Integer billingId;

    /**
     * Direct project name.
     */
    private String directProjectName;

    /**
     * Direct project id.
     */
    private Integer directProjectId;

    /**
     * List of challenge technologies.
     */
    private List<String> challengeTechnologies;

    /**
     * List of challenge platforms.
     */
    private List<String> challengePlatforms;

    /**
     * Challenge start date.
     */
    private String challengeStartDate;

    /**
     * Challenge end date.
     */
    private String challengeEndDate;

    /**
     * DR points.
     */
    private Double drPoints;

    /**
     * Review cost.
     */
    private Double reviewCost;

    /**
     * Challenge fee.
     */
    private Double challengeFee;

    /**
     * Challenge status.
     */
    private String challengeStatus;

    /**
     * Challenge creator.
     */
    private String challengeCreator;

    /**
     * Empty constructor.
     */
    public Challenge() {
    }

    /**
     * Returns the resource path for this entity.
     *
     * @return {@value #RESOURCE_PATH}
     */
    @JsonIgnore
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return RESOURCE_PATH;
    }

    /**
     * Gets the value of the field <code>challengeName</code>.
     *
     * @return the challengeName
     */
    public String getChallengeName() {
        return challengeName;
    }

    /**
     * Sets the value of the field <code>challengeName</code>.
     *
     * @param challengeName the challengeName to set
     */
    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    /**
     * Gets the value of the field <code>challengeType</code>.
     *
     * @return the challengeType
     */
    public String getChallengeType() {
        return challengeType;
    }

    /**
     * Sets the value of the field <code>challengeType</code>.
     *
     * @param challengeType the challengeType to set
     */
    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    /**
     * Gets the value of the field <code>clientName</code>.
     *
     * @return the clientName
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the value of the field <code>clientName</code>.
     *
     * @param clientName the clientName to set
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Gets the value of the field <code>clientId</code>.
     *
     * @return the clientId
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the field <code>clientId</code>.
     *
     * @param clientId the clientId to set
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the value of the field <code>billingName</code>.
     *
     * @return the billingName
     */
    public String getBillingName() {
        return billingName;
    }

    /**
     * Sets the value of the field <code>billingName</code>.
     *
     * @param billingName the billingName to set
     */
    public void setBillingName(String billingName) {
        this.billingName = billingName;
    }

    /**
     * Gets the value of the field <code>billingId</code>.
     *
     * @return the billingId
     */
    public Integer getBillingId() {
        return billingId;
    }

    /**
     * Sets the value of the field <code>billingId</code>.
     *
     * @param billingId the billingId to set
     */
    public void setBillingId(Integer billingId) {
        this.billingId = billingId;
    }

    /**
     * Gets the value of the field <code>directProjectName</code>.
     *
     * @return the directProjectName
     */
    public String getDirectProjectName() {
        return directProjectName;
    }

    /**
     * Sets the value of the field <code>directProjectName</code>.
     *
     * @param directProjectName the directProjectName to set
     */
    public void setDirectProjectName(String directProjectName) {
        this.directProjectName = directProjectName;
    }

    /**
     * Gets the value of the field <code>directProjectId</code>.
     *
     * @return the directProjectId
     */
    public Integer getDirectProjectId() {
        return directProjectId;
    }

    /**
     * Sets the value of the field <code>directProjectId</code>.
     *
     * @param directProjectId the directProjectId to set
     */
    public void setDirectProjectId(Integer directProjectId) {
        this.directProjectId = directProjectId;
    }

    /**
     * Gets the value of the field <code>challengeTechnologies</code>.
     *
     * @return the challengeTechnologies
     */
    public List<String> getChallengeTechnologies() {
        return challengeTechnologies;
    }

    /**
     * Sets the value of the field <code>challengeTechnologies</code>.
     *
     * @param challengeTechnologies the challengeTechnologies to set
     */
    public void setChallengeTechnologies(List<String> challengeTechnologies) {
        this.challengeTechnologies = challengeTechnologies;
    }

    /**
     * Gets the value of the field <code>challengePlatforms</code>.
     *
     * @return the challengePlatforms
     */
    public List<String> getChallengePlatforms() {
        return challengePlatforms;
    }

    /**
     * Sets the value of the field <code>challengePlatforms</code>.
     *
     * @param challengePlatforms the challengePlatforms to set
     */
    public void setChallengePlatforms(List<String> challengePlatforms) {
        this.challengePlatforms = challengePlatforms;
    }

    /**
     * Gets the value of the field <code>challengeStartDate</code>.
     *
     * @return the challengeStartDate
     */
    public String getChallengeStartDate() {
        return challengeStartDate;
    }

    /**
     * Sets the value of the field <code>challengeStartDate</code>.
     *
     * @param challengeStartDate the challengeStartDate to set
     */
    public void setChallengeStartDate(String challengeStartDate) {
        this.challengeStartDate = challengeStartDate;
    }

    /**
     * Gets the value of the field <code>challengeEndDate</code>.
     *
     * @return the challengeEndDate
     */
    public String getChallengeEndDate() {
        return challengeEndDate;
    }

    /**
     * Sets the value of the field <code>challengeEndDate</code>.
     *
     * @param challengeEndDate the challengeEndDate to set
     */
    public void setChallengeEndDate(String challengeEndDate) {
        this.challengeEndDate = challengeEndDate;
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
     * Gets the value of the field <code>reviewCost</code>.
     *
     * @return the reviewCost
     */
    public Double getReviewCost() {
        return reviewCost;
    }

    /**
     * Sets the value of the field <code>reviewCost</code>.
     *
     * @param reviewCost the reviewCost to set
     */
    public void setReviewCost(Double reviewCost) {
        this.reviewCost = reviewCost;
    }

    /**
     * Gets the value of the field <code>challengeFee</code>.
     *
     * @return the challengeFee
     */
    public Double getChallengeFee() {
        return challengeFee;
    }

    /**
     * Sets the value of the field <code>challengeFee</code>.
     *
     * @param challengeFee the challengeFee to set
     */
    public void setChallengeFee(Double challengeFee) {
        this.challengeFee = challengeFee;
    }

    /**
     * Gets the value of the field <code>challengeStatus</code>.
     *
     * @return the challengeStatus
     */
    public String getChallengeStatus() {
        return challengeStatus;
    }

    /**
     * Sets the value of the field <code>challengeStatus</code>.
     *
     * @param challengeStatus the challengeStatus to set
     */
    public void setChallengeStatus(String challengeStatus) {
        this.challengeStatus = challengeStatus;
    }


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
     * Reliability bonus.
     */
    private Double reliabilityBonus;

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

    /**
     * Gets the value of the field <code>challengeCreator</code>.
     *
     * @return the challengeCreator
     */
    public String getChallengeCreator() {
        return challengeCreator;
    }

    /**
     * Sets the value of the field <code>challengeCreator</code>.
     *
     * @param challengeCreator the challengeCreator to set
     */
    public void setChallengeCreator(String challengeCreator) {
        this.challengeCreator = challengeCreator;
    }
}
