package org.oagi.srt.gateway.http.api.release_management.service;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.ReleaseState;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.ReleaseRecord;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.release_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class ReleaseService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ReleaseRepository repository;

    public List<SimpleRelease> getSimpleReleases() {
        return dslContext.select(Tables.RELEASE.RELEASE_ID, Tables.RELEASE.RELEASE_NUM, Tables.RELEASE.STATE)
                .from(Tables.RELEASE)
                .where(Tables.RELEASE.STATE.eq(ReleaseState.Final.getValue()))
                .fetch().map(row -> {
                    SimpleRelease simpleRelease = new SimpleRelease();
                    simpleRelease.setReleaseId(row.getValue(RELEASE.RELEASE_ID).longValue());
                    simpleRelease.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    simpleRelease.setState(ReleaseState.valueOf(row.getValue(RELEASE.STATE)));
                    return simpleRelease;
                });
    }

    public SimpleRelease getSimpleReleaseByReleaseId(long releaseId) {
        return dslContext.select(Tables.RELEASE.RELEASE_ID, Tables.RELEASE.RELEASE_NUM)
                .from(Tables.RELEASE)
                .where(Tables.RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchOneInto(SimpleRelease.class);
    }

    public List<ReleaseList> getReleaseList(User user) {
        List<ReleaseList> releaseLists = dslContext.select(Tables.RELEASE.RELEASE_ID,
                Tables.RELEASE.RELEASE_NUM,
                Tables.RELEASE.LAST_UPDATE_TIMESTAMP,
                Tables.RELEASE.STATE.as("raw_state"),
                Tables.NAMESPACE.URI.as("namespace"),
                Tables.APP_USER.LOGIN_ID.as("last_updated_by"))
                .from(Tables.RELEASE)
                .join(Tables.NAMESPACE)
                .on(Tables.RELEASE.NAMESPACE_ID.eq(Tables.NAMESPACE.NAMESPACE_ID))
                .join(Tables.APP_USER)
                .on(Tables.RELEASE.LAST_UPDATED_BY.eq(Tables.APP_USER.APP_USER_ID))
                .fetchInto(ReleaseList.class);

        releaseLists.forEach(releaseList -> {
            releaseList.setState(ReleaseState.valueOf(releaseList.getRawState()).name());
        });
        return releaseLists;
    }

    private SelectOnConditionStep<Record6<
            ULong, String, Integer, String, String,
            Timestamp>> getSelectOnConditionStep() {
        return dslContext.select(
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                RELEASE.STATE.as("raw_state"),
                NAMESPACE.URI.as("namespace"),
                APP_USER.as("updater").LOGIN_ID.as("last_updated_by"),
                RELEASE.LAST_UPDATE_TIMESTAMP)
                .from(RELEASE)
                .join(APP_USER.as("updater")).on(APP_USER.as("updater").APP_USER_ID.eq(RELEASE.LAST_UPDATED_BY))
                .leftJoin(NAMESPACE).on(RELEASE.NAMESPACE_ID.eq(NAMESPACE.NAMESPACE_ID));
    }

    public PageResponse<ReleaseList> getReleases(User user, ReleaseListRequest request) {
        SelectOnConditionStep<Record6<
                ULong, String, Integer, String, String,
                Timestamp>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getReleaseNum())) {
            conditions.add(RELEASE.RELEASE_NUM.containsIgnoreCase(request.getReleaseNum().trim()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(RELEASE.RELEASE_NUM.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getNamespace())) {
            conditions.add(NAMESPACE.URI.containsIgnoreCase(request.getNamespace().trim()));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(RELEASE.STATE.in(request.getStates().stream().map(e -> e.getValue()).collect(Collectors.toList())));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(RELEASE.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(RELEASE.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        SelectConnectByStep<Record6<
                ULong, String, Integer, String, String,
                Timestamp>> conditionStep = step.where(conditions);
        PageRequest pageRequest = request.getPageRequest();
        String sortDirection = pageRequest.getSortDirection();
        SortField sortField = null;
        switch (pageRequest.getSortActive()) {
            case "releaseNum":
                if ("asc".equals(sortDirection)) {
                    sortField = RELEASE.RELEASE_NUM.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = RELEASE.RELEASE_NUM.desc();
                }

                break;

            case "lastUpdateTimestamp":
                if ("asc".equals(sortDirection)) {
                    sortField = RELEASE.LAST_UPDATE_TIMESTAMP.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = RELEASE.LAST_UPDATE_TIMESTAMP.desc();
                }

                break;
        }
        int pageCount = dslContext.fetchCount(conditionStep);
        SelectWithTiesAfterOffsetStep<Record6<
                ULong, String, Integer, String, String,
                Timestamp>> offsetStep = null;
        if (sortField != null) {
            offsetStep = conditionStep.orderBy(sortField)
                    .limit(pageRequest.getOffset(), pageRequest.getPageSize());
        } else {
            if (pageRequest.getPageIndex() >= 0 && pageRequest.getPageSize() > 0) {
                offsetStep = conditionStep
                        .limit(pageRequest.getOffset(), pageRequest.getPageSize());
            }
        }

        List<ReleaseList> result = (offsetStep != null) ?
                offsetStep.fetchInto(ReleaseList.class) : conditionStep.fetchInto(ReleaseList.class);
        result.forEach(releaseList -> {
            releaseList.setState(ReleaseState.valueOf(releaseList.getRawState()).name());
        });

        PageResponse<ReleaseList> response = new PageResponse();
        response.setList(result);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(pageCount);
        return response;
    }

    @Transactional
    public ReleaseResponse createRelease(User user, ReleaseDetail releaseDetail) {
        long userId = sessionService.userId(user);
        ReleaseResponse response = new ReleaseResponse();
        if (!repository.findByReleaseNum(releaseDetail.getReleaseNum()).isEmpty()) {
            response.setStatus("Fail");
            response.setStatusMessage("'" + releaseDetail.getReleaseNum() + "' is already exist.");
            response.setReleaseDetail(releaseDetail);
            return response;
        }

        ReleaseRecord releaseRecord = repository.create(userId, releaseDetail.getReleaseNum(),
                releaseDetail.getReleaseNote(), releaseDetail.getNamespaceId());

        response.setStatus("success");
        response.setStatusMessage("");
        releaseDetail.setReleaseId(releaseRecord.getReleaseId().longValue());
        releaseDetail.setReleaseNum(releaseRecord.getReleaseNum());
        releaseDetail.setReleaseNote(releaseRecord.getReleaseNote());
        releaseDetail.setNamespaceId(releaseRecord.getNamespaceId().longValue());
        response.setReleaseDetail(releaseDetail);
        return response;
    }
}
