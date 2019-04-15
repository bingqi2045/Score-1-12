package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.gateway.http.api.bie_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.or;
import static org.oagi.srt.data.BieState.*;
import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;
import static org.oagi.srt.gateway.http.helper.SrtJdbcTemplate.newSqlParameterSource;

@Service
@Transactional(readOnly = true)
public class BieService {

    @Autowired
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;

    @Autowired
    private DSLContext dslContext;

    public List<AsccpForBie> getAsccpListForBie(long releaseId) {
        List<AsccpForBie> asccpForBieList = dslContext.select(
                Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.ASCCP.MODULE_ID,
                Tables.MODULE.MODULE_.as("module"),
                Tables.ASCCP.STATE,
                Tables.ASCCP.REVISION_NUM,
                Tables.ASCCP.REVISION_TRACKING_NUM,
                Tables.ASCCP.RELEASE_ID,
                Tables.ASCCP.LAST_UPDATE_TIMESTAMP)
                .from(Tables.ASCCP)
                .leftJoin(Tables.MODULE).on(Tables.ASCCP.MODULE_ID.eq(Tables.MODULE.MODULE_ID))
                .where(and(Tables.ASCCP.REVISION_NUM.greaterThan(0),
                        Tables.ASCCP.STATE.eq(CcState.Published.getValue())))
                .fetchInto(AsccpForBie.class);

        Map<String, List<AsccpForBie>> groupingByGuidAsccpForBieList =
                asccpForBieList.stream()
                        .collect(groupingBy(AsccpForBie::getGuid));

        asccpForBieList = groupingByGuidAsccpForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return asccpForBieList;
    }


    @Transactional
    public BieCreateResponse createBie(User user, BieCreateRequest request) {

        long userId = sessionService.userId(user);
        long releaseId = request.getReleaseId();
        long topLevelAbieId = repository.createTopLevelAbie(userId, releaseId, Editing);

        long asccpId = request.getAsccpId();
        AccForBie accForBie = findRoleOfAccByAsccpId(asccpId, releaseId);

        long basedAccId = accForBie.getAccId();
        long bizCtxId = request.getBizCtxId();

        long abieId = repository.createAbie(user, basedAccId, bizCtxId, topLevelAbieId);
        repository.createAsbiep(user, asccpId, abieId, topLevelAbieId);

        repository.updateAbieIdOnTopLevelAbie(abieId, topLevelAbieId);

        BieCreateResponse response = new BieCreateResponse();
        response.setTopLevelAbieId(topLevelAbieId);
        return response;
    }


    private String FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT =
            "SELECT acc.acc_id, acc.guid, acc.revision_num, acc.revision_tracking_num, acc.revision_action, acc.release_id " +
                    "FROM acc JOIN asccp ON acc.current_acc_id = asccp.role_of_acc_id WHERE asccp.asccp_id = :asccp_id";

    private AccForBie findRoleOfAccByAsccpId(long asccpId, long releaseId) {
        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("asccp_id", asccpId);

        List<AccForBie> accForBieList =
                jdbcTemplate.queryForList(FIND_ROLE_OF_ACC_BY_ASCCP_ID_STATEMENT, parameterSource,
                        AccForBie.class);

        Map<String, List<AccForBie>> groupingByGuidAccForBieList =
                accForBieList.stream()
                        .collect(groupingBy(AccForBie::getGuid));

        accForBieList = groupingByGuidAccForBieList.values().stream()
                .map(e -> CcUtility.getLatestEntity(releaseId, e))
                .filter(e -> e != null)
                .collect(Collectors.toList());

        return (accForBieList.isEmpty()) ? null : accForBieList.get(0);
    }

    private SelectOnConditionStep<Record13<
            ULong, String, String, String, ULong,
            String, ULong, String, String, String,
            Timestamp, String, Integer>> getSelectOnConditionStep() {
        return dslContext.select(
                Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.PROPERTY_TERM,
                Tables.RELEASE.RELEASE_NUM,
                Tables.BIZ_CTX.BIZ_CTX_ID,
                Tables.BIZ_CTX.NAME.as("biz_ctx_name"),
                Tables.TOP_LEVEL_ABIE.OWNER_USER_ID,
                Tables.APP_USER.LOGIN_ID.as("owner"),
                Tables.ABIE.VERSION,
                Tables.ABIE.STATUS,
                Tables.ABIE.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_update_user"),
                Tables.ABIE.STATE.as("raw_state"))
                .from(Tables.TOP_LEVEL_ABIE)
                .join(Tables.ABIE).on(Tables.TOP_LEVEL_ABIE.ABIE_ID.eq(Tables.ABIE.ABIE_ID))
                .and(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID))
                .join(Tables.ASBIEP).on(Tables.ASBIEP.ROLE_OF_ABIE_ID.eq(Tables.ABIE.ABIE_ID))
                .join(Tables.ASCCP).on(Tables.ASCCP.ASCCP_ID.eq(Tables.ASBIEP.BASED_ASCCP_ID))
                .join(Tables.BIZ_CTX).on(Tables.BIZ_CTX.BIZ_CTX_ID.eq(Tables.ABIE.BIZ_CTX_ID))
                .join(Tables.APP_USER).on(Tables.APP_USER.APP_USER_ID.eq(Tables.TOP_LEVEL_ABIE.OWNER_USER_ID))
                .join(Tables.APP_USER.as("updater")).on(Tables.APP_USER.as("updater").APP_USER_ID.eq(Tables.ABIE.LAST_UPDATED_BY))
                .join(Tables.RELEASE).on(Tables.RELEASE.RELEASE_ID.eq(Tables.TOP_LEVEL_ABIE.RELEASE_ID));
    }

    public PageResponse<BieList> getBieList(User user, BieListRequest request) {
        SelectOnConditionStep<Record13<
                ULong, String, String, String, ULong,
                String, ULong, String, String, String,
                Timestamp, String, Integer>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getPropertyTerm())) {
            conditions.add(Tables.ASCCP.PROPERTY_TERM.contains(request.getPropertyTerm().trim()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(Tables.ASCCP.PROPERTY_TERM.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getBusinessContext())) {
            conditions.add(Tables.BIZ_CTX.NAME.contains(request.getBusinessContext().trim()));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(Tables.ABIE.STATE.in(request.getStates().stream().map(e -> e.getValue()).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(APP_USER.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(Tables.ABIE.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(Tables.ABIE.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }
        if (!StringUtils.isEmpty(request.getAccess())) {
            switch (request.getAccess()) {
                case "CanEdit":
                    conditions.add(
                            and(
                                    Tables.ABIE.STATE.notEqual(Initiating.getValue()),
                                    Tables.TOP_LEVEL_ABIE.OWNER_USER_ID.eq(ULong.valueOf(sessionService.userId(user)))
                            )
                    );
                    break;

                case "CanView":
                    conditions.add(
                            or(
                                    Tables.ABIE.STATE.in(Candidate.getValue(), Published.getValue()),
                                    and(
                                            Tables.ABIE.STATE.notEqual(Initiating.getValue()),
                                            Tables.TOP_LEVEL_ABIE.OWNER_USER_ID.eq(ULong.valueOf(sessionService.userId(user)))
                                    )
                            )
                    );
                    break;
            }
        }

        SelectConnectByStep<Record13<
                ULong, String, String, String, ULong,
                String, ULong, String, String, String,
                Timestamp, String, Integer>> conditionStep = step.where(conditions);

        PageRequest pageRequest = request.getPageRequest();
        String sortDirection = pageRequest.getSortDirection();
        SortField sortField = null;
        switch (pageRequest.getSortActive()) {
            case "propertyTerm":
                if ("asc".equals(sortDirection)) {
                    sortField = Tables.ASCCP.PROPERTY_TERM.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = Tables.ASCCP.PROPERTY_TERM.desc();
                }

                break;

            case "releaseNum":
                if ("asc".equals(sortDirection)) {
                    sortField = Tables.RELEASE.RELEASE_NUM.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = Tables.RELEASE.RELEASE_NUM.desc();
                }

                break;

            case "bizCtxName":
                if ("asc".equals(sortDirection)) {
                    sortField = Tables.BIZ_CTX.NAME.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = Tables.BIZ_CTX.NAME.desc();
                }

                break;

            case "lastUpdateTimestamp":
                if ("asc".equals(sortDirection)) {
                    sortField = Tables.ABIE.LAST_UPDATE_TIMESTAMP.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = Tables.ABIE.LAST_UPDATE_TIMESTAMP.desc();
                }

                break;
        }

        SelectWithTiesAfterOffsetStep<Record13<
                ULong, String, String, String, ULong,
                String, ULong, String, String, String,
                Timestamp, String, Integer>> offsetStep = null;
        if (sortField != null) {
            offsetStep = conditionStep.orderBy(sortField)
                    .limit(pageRequest.getOffset(), pageRequest.getPageSize());
        } else {
            if (pageRequest.getPageIndex() >= 0 && pageRequest.getPageSize() > 0) {
                offsetStep = conditionStep
                        .limit(pageRequest.getOffset(), pageRequest.getPageSize());
            }
        }

        List<BieList> result = (offsetStep != null) ?
                offsetStep.fetchInto(BieList.class) : conditionStep.fetchInto(BieList.class);
        result = appendAccessPrivilege(result, user);

        PageResponse<BieList> response = new PageResponse();
        response.setList(result);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(dslContext.selectCount()
                .from(Tables.TOP_LEVEL_ABIE)
                .join(Tables.ABIE).on(Tables.TOP_LEVEL_ABIE.ABIE_ID.eq(Tables.ABIE.ABIE_ID))
                .and(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(Tables.ABIE.OWNER_TOP_LEVEL_ABIE_ID))
                .join(Tables.ASBIEP).on(Tables.ASBIEP.ROLE_OF_ABIE_ID.eq(Tables.ABIE.ABIE_ID))
                .join(Tables.ASCCP).on(Tables.ASCCP.ASCCP_ID.eq(Tables.ASBIEP.BASED_ASCCP_ID))
                .join(Tables.BIZ_CTX).on(Tables.BIZ_CTX.BIZ_CTX_ID.eq(Tables.ABIE.BIZ_CTX_ID))
                .join(Tables.APP_USER).on(Tables.APP_USER.APP_USER_ID.eq(Tables.TOP_LEVEL_ABIE.OWNER_USER_ID))
                .join(Tables.APP_USER.as("updater")).on(Tables.APP_USER.as("updater").APP_USER_ID.eq(Tables.ABIE.LAST_UPDATED_BY))
                .join(Tables.RELEASE).on(Tables.RELEASE.RELEASE_ID.eq(Tables.TOP_LEVEL_ABIE.RELEASE_ID))
                .where(conditions)
                .fetchOptionalInto(Integer.class).orElse(0));

        return response;
    }

    private List<BieList> appendAccessPrivilege(List<BieList> bieLists, User user) {
        long userId = sessionService.userId(user);

        bieLists.stream().forEach(e -> {
            BieState state = BieState.valueOf(e.getRawState());
            e.setState(state);

            AccessPrivilege accessPrivilege = Prohibited;
            switch (state) {
                case Initiating:
                    accessPrivilege = Unprepared;
                    break;

                case Editing:
                    if (userId == e.getOwnerUserId()) {
                        accessPrivilege = CanEdit;
                    } else {
                        accessPrivilege = Prohibited;
                    }
                    break;

                case Candidate:
                    if (userId == e.getOwnerUserId()) {
                        accessPrivilege = CanEdit;
                    } else {
                        accessPrivilege = CanView;
                    }

                    break;

                case Published:
                    accessPrivilege = CanView;
                    break;

            }

            e.setAccess(accessPrivilege.name());
        });

        return bieLists;
    }


    private String GET_BIE_LIST_STATEMENT =
            "SELECT top_level_abie_id, asccp.guid, asccp.property_term, `release`.release_num, " +
                    "biz_ctx.biz_ctx_id, biz_ctx.name as biz_ctx_name, " +
            "top_level_abie.owner_user_id, app_user.login_id as owner, abie.version, abie.`status`, " +
            "abie.last_update_timestamp, top_level_abie.state as raw_state " +
            "FROM top_level_abie " +
            "JOIN abie ON top_level_abie.top_level_abie_id = abie.owner_top_level_abie_id " +
            "AND abie.abie_id = top_level_abie.abie_id " +
            "JOIN asbiep ON asbiep.role_of_abie_id = abie.abie_id " +
            "JOIN asccp ON asbiep.based_asccp_id = asccp.asccp_id " +
            "JOIN biz_ctx ON biz_ctx.biz_ctx_id = abie.biz_ctx_id " +
            "JOIN app_user ON app_user.app_user_id = top_level_abie.owner_user_id " +
            "JOIN `release` ON top_level_abie.release_id = `release`.release_id";


    private List<BieList> getBieList(User user, String whereClauses) {
        List<BieList> bieLists = jdbcTemplate.queryForList(
                GET_BIE_LIST_STATEMENT + (!StringUtils.isEmpty(whereClauses) ? (" WHERE " + whereClauses) : ""), BieList.class);
        return appendAccessPrivilege(bieLists, user);
    }

    public List<BieList> getBieList(GetBieListRequest request) {
        Long bizCtxId = request.getBizCtxId();
        Boolean excludeJsonRelated = request.getExcludeJsonRelated();
        String whereClauses = null;
        if (bizCtxId != null && bizCtxId > 0L) {
            whereClauses = "biz_ctx.biz_ctx_id = " + bizCtxId;
        } else if (excludeJsonRelated != null && excludeJsonRelated == true) {
            whereClauses = "asccp.property_term NOT IN ('Meta Header', 'Pagination Response')";
        }

        return getBieList(request.getUser(), whereClauses);
    }

    public List<BieList> getMetaHeaderBieList(User user) {
        return getBieList(user, "asccp.property_term = 'Meta Header'");
    }

    public List<BieList> getPaginationResponseBieList(User user) {
        return getBieList(user, "asccp.property_term = 'Pagination Response'");
    }

    @Transactional
    public void deleteBieList(List<Long> topLevelAbieIds) {
        if (topLevelAbieIds == null || topLevelAbieIds.isEmpty()) {
            return;
        }

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 0");

        MapSqlParameterSource parameterSource = newSqlParameterSource()
                .addValue("owner_top_level_abie_ids", topLevelAbieIds);

        jdbcTemplate.update("DELETE FROM abie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM asbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbiep WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM bbie_sc WHERE owner_top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);
        jdbcTemplate.update("DELETE FROM top_level_abie WHERE top_level_abie_id IN (:owner_top_level_abie_ids)", parameterSource);

        jdbcTemplate.query("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Transactional
    public void transferOwnership(User user, long topLevelAbieId, String targetLoginId) {
        long ownerAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.eq(user.getUsername()))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (ownerAppUserId == 0L) {
            throw new IllegalArgumentException("Not found an owner user.");
        }

        long targetAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.eq(targetLoginId))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (targetAppUserId == 0L) {
            throw new IllegalArgumentException("Not found a target user.");
        }

        dslContext.update(Tables.TOP_LEVEL_ABIE)
                .set(Tables.TOP_LEVEL_ABIE.OWNER_USER_ID, ULong.valueOf(targetAppUserId))
                .where(and(
                        Tables.TOP_LEVEL_ABIE.OWNER_USER_ID.eq(ULong.valueOf(ownerAppUserId)),
                        Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId))
                ))
                .execute();
    }

}
