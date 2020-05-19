package org.oagi.srt.gateway.http.api.release_management.service;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.enums.ReleaseState;
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

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM, RELEASE.STATE)
                .from(RELEASE)
                .where(RELEASE.STATE.eq(ReleaseState.Published))
                .fetch().map(row -> {
                    SimpleRelease simpleRelease = new SimpleRelease();
                    simpleRelease.setReleaseId(row.getValue(RELEASE.RELEASE_ID).longValue());
                    simpleRelease.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    simpleRelease.setState(row.getValue(RELEASE.STATE));
                    return simpleRelease;
                });
    }

    public SimpleRelease getSimpleReleaseByReleaseId(long releaseId) {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM)
                .from(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchOneInto(SimpleRelease.class);
    }

    public List<ReleaseList> getReleaseList(User user) {
        List<ReleaseList> releaseLists = dslContext.select(
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                RELEASE.STATE,
                APP_USER.as("creator").LOGIN_ID.as("created_by"),
                RELEASE.CREATION_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_updated_by"),
                RELEASE.LAST_UPDATE_TIMESTAMP)
                .from(RELEASE)
                .join(APP_USER.as("creator"))
                .on(RELEASE.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .join(APP_USER.as("updater"))
                .on(RELEASE.LAST_UPDATED_BY.eq(APP_USER.APP_USER_ID))
                .fetchInto(ReleaseList.class);
        return releaseLists;
    }

    private SelectOnConditionStep<Record7<
            ULong, String, ReleaseState, String, LocalDateTime,
            String, LocalDateTime>> getSelectOnConditionStep() {
        return dslContext.select(
                RELEASE.RELEASE_ID,
                RELEASE.RELEASE_NUM,
                RELEASE.STATE,
                APP_USER.as("creator").LOGIN_ID.as("created_by"),
                RELEASE.CREATION_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_updated_by"),
                RELEASE.LAST_UPDATE_TIMESTAMP)
                .from(RELEASE)
                .join(APP_USER.as("creator"))
                .on(RELEASE.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater"))
                .on(RELEASE.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID));
    }

    public PageResponse<ReleaseList> getReleases(User user, ReleaseListRequest request) {
        SelectOnConditionStep<Record7<
                ULong, String, ReleaseState, String, LocalDateTime,
                String, LocalDateTime>> step = getSelectOnConditionStep();

        List<Condition> conditions = new ArrayList();
        if (!StringUtils.isEmpty(request.getReleaseNum())) {
            conditions.add(RELEASE.RELEASE_NUM.containsIgnoreCase(request.getReleaseNum().trim()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(RELEASE.RELEASE_NUM.notIn(request.getExcludes()));
        }
        if (!request.getStates().isEmpty()) {
            conditions.add(RELEASE.STATE.in(request.getStates()));
        }
        if (!request.getCreatorLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("creator").LOGIN_ID.in(request.getCreatorLoginIds()));
        }
        if (request.getCreateStartDate() != null) {
            conditions.add(RELEASE.CREATION_TIMESTAMP.greaterOrEqual(new Timestamp(request.getCreateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getCreateEndDate() != null) {
            conditions.add(RELEASE.CREATION_TIMESTAMP.lessThan(new Timestamp(request.getCreateEndDate().getTime()).toLocalDateTime()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(RELEASE.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(RELEASE.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        SelectConnectByStep<Record7<
                ULong, String, ReleaseState, String, LocalDateTime,
                String, LocalDateTime>> conditionStep = step.where(conditions);
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

            case "state":
                if ("asc".equals(sortDirection)) {
                    sortField = RELEASE.STATE.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = RELEASE.STATE.desc();
                }

                break;

            case "creationTimestamp":
                if ("asc".equals(sortDirection)) {
                    sortField = RELEASE.CREATION_TIMESTAMP.asc();
                } else if ("desc".equals(sortDirection)) {
                    sortField = RELEASE.CREATION_TIMESTAMP.desc();
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
        SelectWithTiesAfterOffsetStep<Record7<
                ULong, String, ReleaseState, String, LocalDateTime,
                String, LocalDateTime>> offsetStep = null;
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

        PageResponse<ReleaseList> response = new PageResponse();
        response.setList(result);
        response.setPage(pageRequest.getPageIndex());
        response.setSize(pageRequest.getPageSize());
        response.setLength(pageCount);
        return response;
    }

    @Transactional
    public ReleaseResponse createRelease(User user, ReleaseDetail releaseDetail) {
        BigInteger userId = sessionService.userId(user);
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
