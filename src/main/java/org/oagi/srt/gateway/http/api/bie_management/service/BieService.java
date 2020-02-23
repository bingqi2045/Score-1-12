package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record10;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.BizCtx;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.gateway.http.api.bie_management.data.*;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.context_management.data.BizCtxAssignment;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.BusinessContextRepository;
import org.oagi.srt.repo.BusinessInformationEntityRepository;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.PaginationResponse;
import org.oagi.srt.repository.ABIERepository;
import org.oagi.srt.repository.BizCtxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.api.common.data.AccessPrivilege.*;

@Service
@Transactional(readOnly = true)
public class BieService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ABIERepository abieRepository;

    @Autowired
    private BizCtxRepository bizCtxRepository;

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private BusinessInformationEntityRepository bieRepository;

    @Autowired
    private BusinessContextRepository businessContextRepository;

    @Autowired
    private DSLContext dslContext;

    @Transactional
    public BieCreateResponse createBie(User user, BieCreateRequest request) {
        AsccpManifestRecord asccpManifest =
                ccRepository.getAsccpManifestByManifestId(request.asccpManifestId());
        if (asccpManifest == null) {
            throw new IllegalArgumentException();
        }

        long userId = sessionService.userId(user);
        long millis = System.currentTimeMillis();

        ULong topLevelAbieId = bieRepository.insertTopLevelAbie()
                .setUserId(userId)
                .setReleaseId(asccpManifest.getReleaseId())
                .setTimestamp(millis)
                .execute();

        ULong abieId = bieRepository.insertAbie()
                .setUserId(userId)
                .setTopLevelAbieId(topLevelAbieId)
                .setAccManifestId(asccpManifest.getRoleOfAccManifestId())
                .setTimestamp(millis)
                .execute();

        bieRepository.insertBizCtxAssignments()
                .setTopLevelAbieId(topLevelAbieId)
                .setBizCtxIds(request.getBizCtxIds())
                .execute();

        bieRepository.insertAsbiep()
                .setAsccpManifestId(asccpManifest.getAsccpManifestId())
                .setRoleOfAbieId(abieId)
                .setTopLevelAbieId(topLevelAbieId)
                .setUserId(userId)
                .setTimestamp(millis)
                .execute();

        bieRepository.updateTopLevelAbie()
                .setAbieId(abieId)
                .setTopLevelAbieId(topLevelAbieId)
                .execute();

        BieCreateResponse response = new BieCreateResponse();
        response.setTopLevelAbieId(topLevelAbieId.longValue());
        return response;
    }

    public PageResponse<BieList> getBieList(User user, BieListRequest request) {
        PageRequest pageRequest = request.getPageRequest();
        long userId = sessionService.userId(user);

        PaginationResponse<BieList> result = bieRepository.selectBieLists()
                .setPropertyTerm(request.getPropertyTerm())
                .setExcludes(request.getExcludes())
                .setStates(request.getStates())
                .setOwnerIds(request.getOwnerLoginIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setUpdaterIds(request.getUpdaterLoginIds().stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                .setUpdateDate(request.getUpdateStartDate(), request.getUpdateEndDate())
                .setAccess(ULong.valueOf(userId), request.getAccess())
                .setSort(pageRequest.getSortActive(), pageRequest.getSortDirection())
                .setOffset(pageRequest.getOffset(), pageRequest.getPageSize())
                .fetchInto(BieList.class);

        List<BieList> bieLists = result.getResult();
        bieLists.forEach(bieList -> {
            bieList.setBusinessContexts(businessContextRepository.selectBusinessContexts()
                    .setTopLevelAbieId(bieList.getTopLevelAbieId())
                    .setName(request.getBusinessContext())
                    .fetchInto(BusinessContext.class).getResult());

            bieList.setAccess(getAccessPrivilege(bieList, userId).name());
        });

        PageResponse<BieList> response = new PageResponse();
        response.setList(bieLists);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(result.getPageCount());
        return response;
    }

    private AccessPrivilege getAccessPrivilege(BieList bieList, long userId) {
        BieState state = BieState.valueOf(bieList.getRawState());
        bieList.setState(state);

        AccessPrivilege accessPrivilege = Prohibited;
        switch (state) {
            case Initiating:
                accessPrivilege = Unprepared;
                break;

            case WIP:
                if (userId == bieList.getOwnerUserId()) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = Prohibited;
                }
                break;

            case QA:
                if (userId == bieList.getOwnerUserId()) {
                    accessPrivilege = CanEdit;
                } else {
                    accessPrivilege = CanView;
                }

                break;

            case Production:
                accessPrivilege = CanView;
                break;
        };

        return accessPrivilege;
    }

    private List<BieList> getBieList(User user, Condition condition) {
        long userId = sessionService.userId(user);

        SelectOnConditionStep<Record10<
                ULong, String, String, String,
                ULong, String, String, String,
                Timestamp, Integer>> selectOnConditionStep = dslContext.select(
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM,
                RELEASE.RELEASE_NUM,
                TOP_LEVEL_ABIE.OWNER_USER_ID,
                APP_USER.LOGIN_ID.as("owner"),
                ABIE.VERSION,
                ABIE.STATUS,
                TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP,
                TOP_LEVEL_ABIE.STATE.as("raw_state"))
                .from(TOP_LEVEL_ABIE)
                .join(ABIE).on(and(
                        TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ABIE.OWNER_TOP_LEVEL_ABIE_ID),
                        TOP_LEVEL_ABIE.ABIE_ID.eq(ABIE.ABIE_ID)))
                .join(ASBIEP).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ASCCP.ASCCP_ID))
                .join(APP_USER).on(APP_USER.APP_USER_ID.eq(TOP_LEVEL_ABIE.OWNER_USER_ID))
                .join(RELEASE).on(RELEASE.RELEASE_ID.eq(TOP_LEVEL_ABIE.RELEASE_ID));

        List<BieList> bieLists;
        if (condition != null) {
            bieLists = selectOnConditionStep.where(condition).fetchInto(BieList.class);
        } else {
            bieLists = selectOnConditionStep.fetchInto(BieList.class);
        }

        bieLists.forEach(bieList -> {
            bieList.setBusinessContexts(businessContextRepository.selectBusinessContexts()
                    .setTopLevelAbieId(bieList.getTopLevelAbieId())
                    .fetchInto(BusinessContext.class).getResult());

            bieList.setAccess(getAccessPrivilege(bieList, userId).name());
        });

        return bieLists;
    }

    public List<BieList> getBieList(GetBieListRequest request) {
        Long bizCtxId = request.getBizCtxId();
        Boolean excludeJsonRelated = request.getExcludeJsonRelated();
        Condition condition = null;
        if (bizCtxId != null && bizCtxId > 0L) {
            List<ULong> topLevelAbieIds =
                    dslContext.select(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID)
                    .from(BIZ_CTX_ASSIGNMENT)
                    .where(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                    .fetchInto(ULong.class);
            if (topLevelAbieIds.isEmpty()) {
                return Collections.emptyList();
            }

            condition = TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.in(topLevelAbieIds);
        } else if (excludeJsonRelated != null && excludeJsonRelated == true) {
            condition = ASCCP.PROPERTY_TERM.notIn("Meta Header", "Pagination Response");
        }

        return getBieList(request.getUser(), condition);
    }

    public BizCtx findBizCtxByAbieId(long abieId) {
        long topLevelAbieId = abieRepository.findById(abieId).getOwnerTopLevelAbieId();
        // return the first biz ctx of the specific topLevelAbieId
        TopLevelAbie top = new TopLevelAbie();
        top.setTopLevelAbieId(topLevelAbieId);
        return bizCtxRepository.findByTopLevelAbie(top).get(0);
    }

    public List<BieList> getMetaHeaderBieList(User user) {
        return getBieList(user, ASCCP.PROPERTY_TERM.eq("Meta Header"));
    }

    public List<BieList> getPaginationResponseBieList(User user) {
        return getBieList(user, ASCCP.PROPERTY_TERM.eq("Pagination Response"));
    }

    @Transactional
    public void deleteBieList(List<Long> topLevelAbieIds) {
        if (topLevelAbieIds == null || topLevelAbieIds.isEmpty()) {
            return;
        }

        dslContext.query("SET FOREIGN_KEY_CHECKS = 0").execute();

        dslContext.deleteFrom(ABIE).where(ABIE.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();
        dslContext.deleteFrom(ASBIE).where(ASBIE.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();
        dslContext.deleteFrom(ASBIEP).where(ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();

        dslContext.deleteFrom(Tables.BBIE).where(BBIE.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();
        dslContext.deleteFrom(Tables.BBIEP).where(BBIEP.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();

        dslContext.deleteFrom(Tables.BBIE_SC).where(BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();
        dslContext.deleteFrom(Tables.TOP_LEVEL_ABIE).where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();
        dslContext.deleteFrom(Tables.BIZ_CTX_ASSIGNMENT).where(Tables.BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.in(topLevelAbieIds)).execute();

        dslContext.query("SET FOREIGN_KEY_CHECKS = 1").execute();
    }

    @Transactional
    public void transferOwnership(User user, long topLevelAbieId, String targetLoginId) {
        long ownerAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.equalIgnoreCase(user.getUsername()))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (ownerAppUserId == 0L) {
            throw new IllegalArgumentException("Not found an owner user.");
        }

        long targetAppUserId = dslContext.select(APP_USER.APP_USER_ID)
                .from(APP_USER)
                .where(APP_USER.LOGIN_ID.equalIgnoreCase(targetLoginId))
                .fetchOptionalInto(Long.class).orElse(0L);
        if (targetAppUserId == 0L) {
            throw new IllegalArgumentException("Not found a target user.");
        }

        dslContext.update(TOP_LEVEL_ABIE)
                .set(TOP_LEVEL_ABIE.OWNER_USER_ID, ULong.valueOf(targetAppUserId))
                .where(and(
                        TOP_LEVEL_ABIE.OWNER_USER_ID.eq(ULong.valueOf(ownerAppUserId)),
                        TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId))
                ))
                .execute();
    }

    @Transactional
    public List<BizCtxAssignment> getAssignBizCtx(long topLevelAbieId) {
        return dslContext.select(
                BIZ_CTX_ASSIGNMENT.BIZ_CTX_ASSIGNMENT_ID,
                BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID,
                BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID)
                .from(BIZ_CTX_ASSIGNMENT)
                .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchInto(BizCtxAssignment.class);
    }

     @Transactional
     public void assignBizCtx(User user, long topLevelAbieId, Collection<Long> biz_ctx_list) {
         ArrayList<Long> newList = new ArrayList<>(biz_ctx_list);
         //remove all records of previous assignment if not in the current assignment
         dslContext.delete(BIZ_CTX_ASSIGNMENT)
                 .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                 .execute();

        for (int i=0; i < newList.size() ; i++) {
            dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                    .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                    .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, ULong.valueOf(newList.get(i)))
                    .onDuplicateKeyIgnore()
                    .execute();
            //if a couple (biz ctx id , toplevelabieId) already exist dont insert it - just update it.
         }

     }


}
