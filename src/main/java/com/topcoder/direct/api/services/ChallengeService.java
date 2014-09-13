/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.api.services;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.metadata.CountableMetadata;
import com.appirio.tech.core.api.v2.metadata.Metadata;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.FilterParameter;
import com.appirio.tech.core.api.v2.request.LimitQuery;
import com.appirio.tech.core.api.v2.request.OrderByQuery;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.request.SortOrder;
import com.appirio.tech.core.api.v2.service.AbstractMetadataService;
import com.appirio.tech.core.api.v2.service.RESTQueryService;
import com.topcoder.direct.api.model.Challenge;
import com.topcoder.direct.api.model.MemberPrize;
import com.topcoder.direct.api.model.Prize;
import com.topcoder.direct.api.security.AccessLevel;
import com.topcoder.direct.api.security.DirectAuthenticationToken;
import com.topcoder.direct.api.security.SecurityUtil;
import com.topcoder.direct.dao.CatalogDAO;
import com.topcoder.direct.dao.ChallengeDAO;
import com.topcoder.direct.dao.UserDAO;
import com.topcoder.direct.exception.BadRequestException;
import com.topcoder.direct.exception.ServerInternalException;
import com.topcoder.direct.util.Helper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.topcoder.direct.util.Helper.isNull;

/**
 * This is the service implementation for the My Challenges API.
 *
 * @author j3_guile
 * @version 1.0
 * @since 1.0 (Topcoder Direct API - My Challenges API v1.0)
 */
@Service
public class ChallengeService extends AbstractMetadataService implements RESTQueryService<Challenge> {

    /**
     * Logger instance.
     */
    private static final Logger LOG = Logger.getLogger(ChallengeService.class);

    /**
     * A placeholder to ensure there are no matches returned when search of lookup values
     * return nothing.
     */
    private static final List<Integer> NO_MATCHED_LOOKUP = Arrays.asList(-1);

    /**
     * The default sort field.
     */
    private static final String DEFAULT_SORT_FIELD = "challengeEndDate";

    /**
     * Filter for creator.
     */
    private static final String CREATOR_FILTER = " AND p.create_user = :creator_id\n";

    /**
     * Allowed type values for my created challenges api.
     */
    private static final List<String> ALLOWED_TYPE = Arrays.asList("active", "past", "draft");

    /**
     * The challenge type filter.
     */
    private static final String CHALLENGE_TYPE_FILTER = " AND p.project_category_id IN (:challenge_type_ids)\n";

    /**
     * The technology filter.
     */
    private static final String TECHNOLOGY_FILTER = " AND EXISTS (SELECT DISTINCT 1 FROM comp_technology ct "
        + "WHERE ct.comp_vers_id = pi1.value AND ct.technology_type_id IN (:technology_ids))\n";

    /**
     * The platform filter.
     */
    private static final String PLATFORM_FILTER = " AND EXISTS (SELECT 1 FROM project_platform pp "
        + "WHERE pp.project_platform_id IN (:platform_ids) AND p.project_id = pp.project_id)\n";

    /**
     * The direct project id filter.
     */
    private static final String DIRECT_PROJECT_ID_FILTER = " AND p.tc_direct_project_id IN (:direct_project_ids)\n";

    /**
     * The project id filter.
     */
    private static final String PROJECT_ID_FILTER = " AND p.project_id IN (:project_ids)\n";

    /**
     * The type filter.
     */
    private static final String TYPE_FILTER = " AND p.project_status_id IN (:type_id)\n";

    /**
     * The active project status name.
     */
    private static final String ACTIVE_PROJECT_STATUS = "active";

    /**
     * The draft project status name.
     */
    private static final String DRAFT_PROJECT_STATUS = "draft";

    /**
     * The challenge prize type id.
     */
    private static final Integer CHALLENGE_PRIZE_TYPE = 15;

    /**
     * The checkpoint prize type id.
     */
    private static final Integer CHECKPOINT_PRIZE_TYPE = 14;

    /**
     * Field mapping for ordering.
     */
    private static final Map<String, String> ORDER_BY_FIELDS = new HashMap<String, String>() {
        {
            put("id", "challenge_id");
            put("challengename", "challenge_name");
            put("challengetype", "challenge_type");
            put("clientname", "client_name");
            put("clientid", "client_id");
            put("billingname", "billing_name");
            put("billingid", "billing_id");
            put("directprojectname", "direct_project_name");
            put("directprojectid", "direct_project_id");
            put("challengestartdate", "challenge_start_date");
            put("challengeenddate", "challenge_end_date");
            put("drpoints", "dr_points");
            put("challengestatus", "challenge_status");
        }
    };

    /**
     * The challenge DAO.
     */
    @Autowired
    private ChallengeDAO challengeDAO;

    /**
     * The catalog DAO.
     */
    @Autowired
    private CatalogDAO catalogDAO;

    /**
     * The user dao that used to retrieve data from database.
     */
    @Autowired
    private UserDAO userDAO;

    /**
     * Creates a new instance. Initializes the field map.
     */
    public ChallengeService() {
    }

    /**
     * Returns the resource path for challenges.
     *
     * @return {@value Challenge.#RESOURCE_PATH}
     */
    @ApiMapping(visible = false)
    public String getResourcePath() {
        return Challenge.RESOURCE_PATH;
    }

    /**
     * Not implemented.
     *
     * http://apps.topcoder.com/forums/?module=Thread&threadID=828408&start=0
     *
     * @param selector not used
     * @param recordId not used
     * @return none
     * @throws UnsupportedOperationException always
     */
    public Challenge handleGet(FieldSelector selector, CMCID recordId) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Retrieves challenges using the provided specification.
     *
     * @param request the servlet request
     * @param query the filter and output specification
     * @return the challenge data requested, may be empty, never null
     * @throws BadRequestException for any validation failure in the request data
     * @throws UnauthorizedException if the request principal is not logged in as a member or admin roles
     * @throws ServerInternalException for IOExceptions caught from the DAO
     * Note: all RuntimeExceptions are propagated to the framework exception handlers.
     */
    public List<Challenge> handleGet(HttpServletRequest request, QueryParameter query) {
        DirectAuthenticationToken identity = SecurityUtil.getAuthentication(request);
        identity.authorize(AccessLevel.ADMIN, AccessLevel.MEMBER);

        List<Challenge> challenges = new ArrayList<Challenge>();
        try {
            Map<String, Object> sqlParameters = new HashMap<String, Object>();
            List<String> customFilter = new ArrayList<String>();

            // validate filters
            validateQuery(identity.getUserId(), query);

            // for access checking
            sqlParameters.put("user_id", identity.getUserId());

            if (query.getFilter().contains("creator")) {
                // Perform "My created challenges" flow
                customFilter = getCreatorChallengeFilters(identity, query, sqlParameters);
                sqlParameters.put("creator_id", identity.getUserId());
            } // otherwise, ignore all filters

            // limit results
            populateLimitQuery(query, sqlParameters);
            String orderClause = getOrderClause(query.getOrderByQuery());
            challenges = challengeDAO.getMyChallenge(customFilter, sqlParameters, orderClause);

            if (challenges.size() > 0) {
                mergePrizesToChallenges(challenges, sqlParameters, customFilter);
            }
        } catch (IOException e) {
            throw new ServerInternalException("An error occurred while querying for challenges", e);
        }

        return challenges;
    }

    /**
     * Generates the order by clause for challenges.
     *
     * @param query the order query requested
     * @return the order by clause
     */
    private String getOrderClause(OrderByQuery query) {
        StringBuilder order = new StringBuilder();
        String fldName = query.getOrderByField();
        if (fldName == null || fldName.trim().length() == 0) {
            fldName = DEFAULT_SORT_FIELD;
        }

        fldName = fldName.toLowerCase();
        if (ORDER_BY_FIELDS.get(fldName) == null) {
            throw new BadRequestException("Sorting is not supported for requested field.");
        }

        order.append(" ORDER BY " + ORDER_BY_FIELDS.get(fldName)).append(" ");
        if (query.getSortOrder() != null) { // specified direction
            if (query.getSortOrder() == SortOrder.ASC_NULLS_FIRST) {
                order.append("ASC ");
            } else if (query.getSortOrder() == SortOrder.DESC_NULLS_LAST) {
                order.append("DESC ");
            } else {
                throw new BadRequestException("Specified sort order is not supported. " + query.getSortOrder());
            }
        }

        // add a secondary sorting for non unique sort requests
        if (!"id".equalsIgnoreCase(fldName)) {
            order.append(", challenge_id DESC");
        }

        order.append("\n");
        return order.toString();
    }

    /**
     * Populates the SQL parameters for pagination. Uses LimitQuery.
     *
     * @param query The query parameters of request.
     * @param sqlParameters Current SQL parameters.
     */
    private void populateLimitQuery(QueryParameter query, Map<String, Object> sqlParameters) {
        LimitQuery limitQuery = query.getLimitQuery();
        int offset = 0;
        int pageSize = 10;

        if (limitQuery != null) {
            if (limitQuery.getLimit() != null) {
                pageSize = limitQuery.getLimit();
                if (pageSize == -1) {
                    pageSize = Integer.MAX_VALUE;
                }
            }
            if (limitQuery.getOffset() != null) {
                offset = limitQuery.getOffset();
            }
        }
        sqlParameters.put("page_size", pageSize);
        sqlParameters.put("first_row_index", offset);
    }

    /**
     * Merges the corresponding Prizes to Challenges.
     *
     * @param challenges The challenges to be filled with Prizes.
     * @param sqlParameters The sql parameters to use in querying prizes and challenges.
     * @param customFilter The filter to use in querying
     * @throws IOException When an error occurs while querying the prizes.
     */
    private void mergePrizesToChallenges(List<Challenge> challenges, Map<String, Object> sqlParameters,
        List<String> customFilter) throws IOException {

        // NOTE: get all the projects from the challenges retrieved to query the prizes efficiently
        List<Integer> projectIds = new ArrayList<Integer>();
        for (Challenge challenge : challenges) {
            projectIds.add(Integer.valueOf(challenge.getId().toString()));
        }
        // Get the challenge type id and insert into sqlParameters.
        sqlParameters.put("project_ids", projectIds);
        customFilter.add(PROJECT_ID_FILTER);

        List<Prize> prizes = challengeDAO.getMyChallengesPrizes(customFilter, sqlParameters);

        Map<Integer, List<Prize>> challengeId2PrizeMap = new HashMap<Integer, List<Prize>>();

        for (Prize prize : prizes) {
            Integer challengeId = prize.getChallengeId();
            if (isNull(challengeId2PrizeMap.get(challengeId))) {
                // We don't have prizes for this challenge
                List<Prize> list = new ArrayList<Prize>();
                list.add(prize);
                challengeId2PrizeMap.put(challengeId, list);
            } else {
                // Otherwise
                challengeId2PrizeMap.get(challengeId).add(prize);
            }
        }

        for (Challenge challenge : challenges) {
            List<Prize> challengePrizes = new ArrayList<Prize>();
            List<Prize> checkPointPrizes = new ArrayList<Prize>();
            Double totalPrizes = 0.0;
            List<Prize> prs = challengeId2PrizeMap.get(Integer.valueOf(challenge.getId().toString()));

            MemberPrize memberPrize = new MemberPrize();
            memberPrize.setDrPoints(challenge.getDrPoints());
            if (!isNull(prs)) {
                for (Prize prize : prs) {
                    totalPrizes += prize.getPrizeAmount() * prize.getNumberOfPrize();
                    if (prize.getPrizeType().equals(CHALLENGE_PRIZE_TYPE)) {
                        prize.setNumberOfPrize(null);
                        challengePrizes.add(prize);
                    } else if (prize.getPrizeType().equals(CHECKPOINT_PRIZE_TYPE)) {
                        prize.setPlacement(null);
                        checkPointPrizes.add(prize);
                    } else {
                        // Eliminate any other type prizes from total prizes.
                        totalPrizes -= prize.getPrizeAmount() * prize.getNumberOfPrize();
                    }
                }
                memberPrize.setPrizes(challengePrizes);
                memberPrize.setCheckPointPrizes(checkPointPrizes);
            }
            memberPrize.setTotalPrize(totalPrizes);


            challenge.setPrizes(memberPrize.getPrizes());
            challenge.setCheckPointPrizes(memberPrize.getCheckPointPrizes());
            challenge.setTotalPrize(memberPrize.getTotalPrize());
        }
    }

    /**
     * Validate input parameters for my created challenges.
     *
     * @param userId the current user Id
     * @param query the query.
     * @throws BadRequestException if the any of the query parameters are invalid
     * @throws IOException When an error occurs while getting user handle of logged in user
     */
    private void validateQuery(Integer userId, QueryParameter query) throws IOException {
        FilterParameter filter = query.getFilter();
        if (filter.contains("type")) {
            List<String> values = toValueList(filter.get("type"), true);
            if (!ALLOWED_TYPE.containsAll(values)) {
                throw new BadRequestException("Invalid type. One of [\"active\", \"past\", \"draft\"] expected.");
            }
        }

        if (filter.contains("creator")) {
            List<String> values = toValueList(filter.get("creator"), false);
            // we currently only allow the current user for this filter
            // http://apps.topcoder.com/forums/?module=Thread&threadID=828530&start=0
            String currentHandle = userDAO.getUserHandle(userId);
            for (String handle : values) {
                if (!currentHandle.equals(handle)) {
                    throw new BadRequestException("Invalid creator, only current user is supported.");
                }
            }
        }

        if (filter.contains("directProjectId")) {
            List<String> values = toValueList(filter.get("directProjectId"), false);
            for (String val : values) {
                try {
                    int id = Integer.parseInt(val);
                    if (id <= 0) {
                        throw new BadRequestException("Direct Project Id should be positive.");
                    }
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException("Invalid directProjectId.");
                }
            }
        }

        // check limits, technically this should be done by the framework since it built the query object
        // but for compatibility with the existing code, we perform some checks for now
        LimitQuery limitQuery = query.getLimitQuery();
        if (limitQuery != null) {
            if (limitQuery.getLimit() != null) {
                if (limitQuery.getLimit() == 0 || limitQuery.getLimit() < -1) {
                    throw new BadRequestException("Invalid limit, -1 if you want to get all records.");
                }
            }
            if (limitQuery.getOffset() != null) {
                if (limitQuery.getOffset() < 0) {
                    throw new BadRequestException("Invalid offset, must be 0 or more.");
                }
            }
        }
    }

    /**
     * Get the challenge filters to be used in the query and set the appropriate sql parameters as well.
     *
     * @param identity the current user
     * @param query The filters and limits to be used in the query.
     * @param sqlParameters The sql parameters object that will be used when execute query.
     * @return The list of filter content that need to add into query manually.
     * @exception IOException If something went wrong when read the query.
     */
    private List<String> getCreatorChallengeFilters(DirectAuthenticationToken identity, QueryParameter query,
        Map<String, Object> sqlParameters) throws IOException {
        FilterParameter filter = query.getFilter();
        List<String> filterToAdd = new ArrayList<String>();

        filterToAdd.add(CREATOR_FILTER);
        sqlParameters.put("creator_id", identity.getUserId());

        if (filter.contains("challengeType")) {
            // Get the challenge type id and insert into sqlParameters.
            List<String> challengeTypes = toValueList(filter.get("challengeType"), false);
            List<Integer> challengeTypeIds = catalogDAO.getIds("challenge_type", challengeTypes, "challenge_types");
            if (challengeTypeIds.isEmpty()) {
                challengeTypeIds = NO_MATCHED_LOOKUP;
            }

            sqlParameters.put("challenge_type_ids", challengeTypeIds);
            filterToAdd.add(CHALLENGE_TYPE_FILTER);
        }

        if (filter.contains("challengeStatus")) {
            List<String> status = toValueList(filter.get("challengeStatus"), true);
            if (!status.isEmpty()) {
                List<String> csFilters = new ArrayList<String>();
                int index = 0;
                for (String filterValue : status) {
                    boolean isNumber = true;
                    Integer challengeStatusId = 0;
                    try {
                        challengeStatusId = Integer.valueOf(filterValue.toString());
                    } catch (NumberFormatException nef) {
                        // Do nothing.
                        isNumber = false;
                    }
                    if (isNumber) {
                        sqlParameters.put("challenge_status_id" + index, challengeStatusId);
                        csFilters.add(" p.project_status_id = :challenge_status_id" + index);
                    } else {
                        sqlParameters.put("challenge_status_name" + index, "%" + filterValue + "%");
                        csFilters.add(" LOWER(psl.name) LIKE :challenge_status_name" + index);
                    }
                    index++;
                }
                filterToAdd.add("AND (" + StringUtils.join(csFilters, " OR ") + ") \n");
            }
        }

        if (filter.contains("type")) {
            List<String> type = toValueList(filter.get("type"), true);
            List<Integer> typeIds = new ArrayList<Integer>();
            if (type.contains(ACTIVE_PROJECT_STATUS)) {
                typeIds.add(1);
            }
            if (type.contains(DRAFT_PROJECT_STATUS)) {
                typeIds.add(2);
            }
            if (type.contains("past")) {
                typeIds.addAll(catalogDAO.getIds("draft_project_status", null, null));
            }
            if (typeIds.isEmpty()) {
                typeIds = NO_MATCHED_LOOKUP;
            }
            sqlParameters.put("type_id", typeIds);
            filterToAdd.add(TYPE_FILTER);
        }

        if (filter.contains("challengeTechnologies")) {
            List<String> technologies = toValueList(filter.get("challengeTechnologies"), true);
            List<Integer> technologyIds = catalogDAO.getIds("technology", technologies, "technologies");
            if (technologyIds.isEmpty()) {
                technologyIds = NO_MATCHED_LOOKUP;
            }

            sqlParameters.put("technology_ids", technologyIds);
            filterToAdd.add(TECHNOLOGY_FILTER);
        }

        if (filter.contains("challengePlatforms")) {
            List<String> platforms = toValueList(filter.get("challengePlatforms"), true);
            List<Integer> platformsIds = catalogDAO.getIds("platform", platforms, "platforms");
            if (platformsIds.isEmpty()) {
                platformsIds = NO_MATCHED_LOOKUP;
            }
            // Get platform id and insert them to sqlParameters.
            sqlParameters.put("platform_ids", platformsIds);
            filterToAdd.add(PLATFORM_FILTER);
        }

        if (filter.contains("directProjectId")) {
            List<String> values = toValueList(filter.get("directProjectId"), true);
            List<Integer> projectIds = new ArrayList<Integer>();
            for (String id : values) {
                projectIds.add(Integer.valueOf(id));
            }
            sqlParameters.put("direct_project_ids", projectIds);
            filterToAdd.add(DIRECT_PROJECT_ID_FILTER);
        }

        if (filter.contains("directProjectName")) {
            List<String> values = toValueList(filter.get("directProjectName"), true);
            if (!values.isEmpty()) {
                List<String> pnameFilters = new ArrayList<String>();
                int index = 0;
                for (String value : values) {
                    sqlParameters.put("direct_project_name" + index, "%" + value + "%");
                    pnameFilters.add("EXISTS (SELECT 1 FROM tc_direct_project tdp "
                        + "WHERE tdp.project_id = p.tc_direct_project_id AND LOWER(tdp.name) "
                        + "LIKE (:direct_project_name" + index + "))");
                    index++;
                }
                filterToAdd.add("AND (" + StringUtils.join(pnameFilters, " OR ") + ") \n");
            }
        }
        return filterToAdd;
    }


    /**
     * Gets the metadata
     *
     * @param request the http servlet request.
     * @param query the QueryParameter instance.
     * @return the metadata
     * @throws Exception if any error.
     */
    @Override
    public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
        CountableMetadata metadata = new CountableMetadata();
        DirectAuthenticationToken identity = SecurityUtil.getAuthentication(request);
        identity.authorize(AccessLevel.ADMIN, AccessLevel.MEMBER);

        try {
            Map<String, Object> sqlParameters = new HashMap<String, Object>();
            List<String> customFilter = new ArrayList<String>();

            // validate filters
            validateQuery(identity.getUserId(), query);

            // for access checking
            sqlParameters.put("user_id", identity.getUserId());

            if (query.getFilter().contains("creator")) {
                // Perform "My created challenges" flow
                customFilter = getCreatorChallengeFilters(identity, query, sqlParameters);
                sqlParameters.put("creator_id", identity.getUserId());
            } // otherwise, ignore all filters

            Integer myChallengesCount = challengeDAO.getMyChallengesCount(customFilter, sqlParameters);

            if (myChallengesCount != null) {
                metadata.setTotalCount(myChallengesCount);
            }
        } catch (IOException e) {
            throw new ServerInternalException("An error occurred while querying for challenges total count", e);
        }

        return metadata;
    }

    /**
     * Strips of "in()" from the given request parameter and parses it as a list. If it is not a list then a single
     * element will be used
     *
     * Per spec:
     * 2) filedValue(sic) can contain multiple values using in() format {fieldName}=in({fieldValue1},{fieldValue1})
     *
     * @param object the parameter object
     * @param forceLowerCase converts the value to lower case
     * @return the list of parsed values
     */
    private List<String> toValueList(Object object, boolean forceLowerCase) {
        List<String> values = new ArrayList<String>();
        if (object != null) {
            String val = object.toString().trim();
            if (forceLowerCase) {
                val = val.toLowerCase();
            }

            String tmp = val.toLowerCase().replaceAll("\\s", ""); // to ignore any space between IN and (
            if (val.toLowerCase().startsWith("in") && tmp.startsWith("in(") && tmp.endsWith(")")) {
                // multiple value format!
                val = val.substring(val.indexOf("(") + 1, val.length() - 1);

                // each item can vary types, we can't just split it based on ',' because it might be a quoted string
                for (String string : Helper.unQuoteAndSplit(val)) {
                    values.add(string);
                }
            } else {
                // single value format
                values.add(Helper.unQuoteString(val));
            }
        }
        LOG.debug("Parsed filter values: " + values);
        return values;
    }
}
