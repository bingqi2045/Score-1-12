package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.BizCtx;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCreateResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.BieList;
import org.oagi.srt.gateway.http.api.bie_management.data.BieListRequest;
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

import java.util.ArrayList;
import java.util.Collection;
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
        }

        return accessPrivilege;
    }

    public BizCtx findBizCtxByAbieId(long abieId) {
        long topLevelAbieId = abieRepository.findById(abieId).getOwnerTopLevelAbieId();
        // return the first biz ctx of the specific topLevelAbieId
        TopLevelAbie top = new TopLevelAbie();
        top.setTopLevelAbieId(topLevelAbieId);
        return bizCtxRepository.findByTopLevelAbie(top).get(0);
    }

    @Transactional
    public void deleteBieList(User requester, List<Long> topLevelAbieIds) {
        if (topLevelAbieIds == null || topLevelAbieIds.isEmpty()) {
            return;
        }

        /*
         * Issue #772
         */
        ensureProperDeleteBieRequest(requester, topLevelAbieIds);

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

    private void ensureProperDeleteBieRequest(User requester, List<Long> topLevelAbieIds) {
        Result<Record2<Integer, ULong>> result =
                dslContext.select(TOP_LEVEL_ABIE.STATE, TOP_LEVEL_ABIE.OWNER_USER_ID)
                        .from(TOP_LEVEL_ABIE)
                        .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.in(
                                topLevelAbieIds.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList())
                        ))
                        .fetch();

        long requesterUserId = sessionService.userId(requester);
        for (Record2<Integer, ULong> record : result) {
            BieState bieState = BieState.valueOf(record.value1());
            if (bieState == BieState.Production) {
                throw new DataAccessForbiddenException("Not allowed to delete the BIE in '" + bieState + "' state.");
            }

            if (requesterUserId != record.value2().longValue()) {
                throw new DataAccessForbiddenException("Only allowed to delete the BIE by the owner.");
            }
        }
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

        for (int i = 0; i < newList.size(); i++) {
            dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                    .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID, ULong.valueOf(topLevelAbieId))
                    .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, ULong.valueOf(newList.get(i)))
                    .onDuplicateKeyIgnore()
                    .execute();
            //if a couple (biz ctx id , toplevelabieId) already exist dont insert it - just update it.
        }

    }


}
