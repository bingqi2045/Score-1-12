package org.oagi.srt.gateway.http.api.release_management.service;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.Release;
import org.oagi.srt.entity.jooq.tables.records.ReleaseRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.graph.GraphService;
import org.oagi.srt.gateway.http.api.release_management.data.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.event.ReleaseCreateRequestEvent;
import org.oagi.srt.redis.event.EventListenerContainer;
import org.oagi.srt.repo.component.release.ReleaseRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.APP_USER;
import static org.oagi.srt.entity.jooq.Tables.RELEASE;

@Service
@Transactional(readOnly = true)
public class ReleaseService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ReleaseRepository repository;

    @Autowired
    private GraphService graphService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private EventListenerContainer eventListenerContainer;

    private String INTERESTED_EVENT_NAME = "releaseCreateRequestEvent";

    @Override
    public void afterPropertiesSet() throws Exception {
        eventListenerContainer.addMessageListener(this, "onEventReceived",
                new ChannelTopic(INTERESTED_EVENT_NAME));
    }

    public List<SimpleRelease> getSimpleReleases(SimpleReleasesRequest request) {
        List<Condition> conditions = new ArrayList();
        if (!request.getStates().isEmpty()) {
            conditions.add(RELEASE.STATE.in(request.getStates()));
        }
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM, RELEASE.STATE)
                .from(RELEASE)
                .where(conditions)
                .fetch().map(row -> {
                    SimpleRelease simpleRelease = new SimpleRelease();
                    simpleRelease.setReleaseId(row.getValue(RELEASE.RELEASE_ID).toBigInteger());
                    simpleRelease.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    simpleRelease.setState(ReleaseState.valueOf(row.getValue(RELEASE.STATE)));
                    return simpleRelease;
                });
    }

    public SimpleRelease getSimpleReleaseByReleaseId(BigInteger releaseId) {
        return dslContext.select(RELEASE.RELEASE_ID, RELEASE.RELEASE_NUM)
                .from(RELEASE)
                .where(RELEASE.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                .fetchOneInto(SimpleRelease.class);
    }

    public List<ReleaseList> getReleaseList(User user) {
        List<ReleaseList> releaseLists = dslContext.select(
                RELEASE.RELEASE_ID,
                RELEASE.GUID,
                RELEASE.RELEASE_NUM,
                RELEASE.RELEASE_NOTE,
                RELEASE.RELEASE_LICENSE,
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

    private SelectOnConditionStep<Record10<
            ULong, String, String, String, String,
            String, String, LocalDateTime, String, LocalDateTime>> getSelectOnConditionStep() {
        return dslContext.select(
                RELEASE.RELEASE_ID,
                RELEASE.GUID,
                RELEASE.RELEASE_NUM,
                RELEASE.RELEASE_NOTE,
                RELEASE.RELEASE_LICENSE,
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
        SelectOnConditionStep<Record10<
                ULong, String, String, String, String,
                String, String, LocalDateTime, String, LocalDateTime>> step = getSelectOnConditionStep();

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

        SelectConnectByStep<Record10<
                ULong, String, String, String, String,
                String, String, LocalDateTime, String, LocalDateTime>> conditionStep = step.where(conditions);
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
        SelectWithTiesAfterOffsetStep<Record10<
                ULong, String, String, String, String,
                String, String, LocalDateTime, String, LocalDateTime>> offsetStep = null;
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

        ReleaseRecord releaseRecord = repository.create(userId,
                releaseDetail.getReleaseNum(),
                releaseDetail.getReleaseNote(),
                releaseDetail.getReleaseLicense(),
                releaseDetail.getNamespaceId());

        response.setStatus("success");
        response.setStatusMessage("");

        releaseDetail.setReleaseId(releaseRecord.getReleaseId().toBigInteger());
        if (releaseRecord.getNamespaceId() != null) {
            releaseDetail.setNamespaceId(releaseRecord.getNamespaceId().toBigInteger());
        }
        releaseDetail.setReleaseNum(releaseRecord.getReleaseNum());
        releaseDetail.setReleaseNote(releaseRecord.getReleaseNote());
        releaseDetail.setReleaseLicense(releaseRecord.getReleaseLicense());
        releaseDetail.setState(releaseRecord.getState());
        response.setReleaseDetail(releaseDetail);

        return response;
    }

    @Transactional
    public void updateRelease(User user, ReleaseDetail releaseDetail) {
        BigInteger userId = sessionService.userId(user);

        repository.update(userId,
                releaseDetail.getReleaseId(),
                releaseDetail.getReleaseNum(),
                releaseDetail.getReleaseNote(),
                releaseDetail.getReleaseLicense(),
                releaseDetail.getNamespaceId());
    }

    public ReleaseDetail getReleaseDetail(User user, BigInteger releaseId) {
        Release release = repository.findById(releaseId);
        ReleaseDetail detail = new ReleaseDetail();
        detail.setReleaseId(release.getReleaseId());
        detail.setNamespaceId(release.getNamespaceId());
        detail.setReleaseNum(release.getReleaseNum());
        detail.setReleaseNote(release.getReleaseNote());
        detail.setReleaseLicense(release.getReleaseLicense());
        detail.setState(release.getState());
        return detail;
    }

    /**
     * This method is invoked by 'bieCopyRequestEvent' channel subscriber.
     *
     * @param releaseCreateRequestEvent
     */
    @Transactional
    public void onEventReceived(ReleaseCreateRequestEvent releaseCreateRequestEvent) {
        RLock lock = redissonClient.getLock("ReleaseCreateRequestEvent:" + releaseCreateRequestEvent.hashCode());
        if (!lock.tryLock()) {
            return;
        }
        try {
            logger.debug("Received ReleaseCreateRequestEvent: " + releaseCreateRequestEvent);

            repository.copyWorkingManifestsTo(releaseCreateRequestEvent.getReleaseId());
        } finally {
            lock.unlock();
        }
    }

    public AssignComponents getAssignComponents(BigInteger releaseId) {
        return repository.getAssignComponents(releaseId);
    }

    @Transactional
    public void assignComponents(User user,
                                 AssignComponentsRequest request) {

        repository.copyWorkingManifestsTo(request.getReleaseId(),
                Arrays.asList(CcState.Candidate),
                request.getAccManifestIds(),
                request.getAsccpManifestIds(),
                request.getBccpManifestIds()
        );
    }

    @Transactional
    public void unassignComponents(User user,
                                   UnassignComponentsRequest request) {

        repository.unassignManifests(request.getReleaseId(),
                request.getAccManifestIds(),
                request.getAsccpManifestIds(),
                request.getBccpManifestIds()
        );
    }

    @Transactional
    public void transitState(User user,
                             TransitStateRequest request) {

        repository.transitState(user, request);
    }

    public ReleaseValidationResponse validate(@AuthenticationPrincipal User user,
                                              @RequestBody ReleaseValidationRequest request) {

        ReleaseValidator validator = new ReleaseValidator(dslContext);
        validator.setAssignedAccComponentManifestIds(request.getAssignedAccComponentManifestIds());
        validator.setAssignedAsccpComponentManifestIds(request.getAssignedAsccpComponentManifestIds());
        validator.setAssignedBccpComponentManifestIds(request.getAssignedBccpComponentManifestIds());
        return validator.validate();
    }
}
