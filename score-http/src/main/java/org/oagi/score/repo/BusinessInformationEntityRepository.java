package org.oagi.score.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccpManifestRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.TopLevelAsbiepRecord;
import org.oagi.score.service.common.data.AccessPrivilege;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static org.oagi.score.gateway.http.helper.Utility.sha256;
import static org.oagi.score.gateway.http.helper.filter.ContainsFilterBuilder.contains;
import static org.oagi.score.repo.api.bie.model.BieState.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class BusinessInformationEntityRepository {

    @Autowired
    private DSLContext dslContext;

    public class InsertTopLevelAsbiepArguments {
        private String releaseId;
        private String userId;
        private BieState bieState = WIP;
        private String version;
        private String status;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertTopLevelAsbiepArguments setReleaseId(String releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public InsertTopLevelAsbiepArguments setBieState(BieState bieState) {
            this.bieState = bieState;
            return this;
        }

        public InsertTopLevelAsbiepArguments setVersion(String version) {
            this.version = version;
            return this;
        }

        public InsertTopLevelAsbiepArguments setStatus(String status) {
            this.status = status;
            return this;
        }

        public InsertTopLevelAsbiepArguments setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public InsertTopLevelAsbiepArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public InsertTopLevelAsbiepArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public InsertTopLevelAsbiepArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public String getReleaseId() {
            return releaseId;
        }

        public BieState getBieState() {
            return bieState;
        }

        public String getVersion() {
            return version;
        }

        public String getStatus() {
            return status;
        }

        public String getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String execute() {
            return insertTopLevelAsbiep(this);
        }
    }

    public InsertTopLevelAsbiepArguments insertTopLevelAsbiep() {
        return new InsertTopLevelAsbiepArguments();
    }

    private String insertTopLevelAsbiep(InsertTopLevelAsbiepArguments arguments) {
        TopLevelAsbiepRecord record = new TopLevelAsbiepRecord();
        record.setTopLevelAsbiepId(UUID.randomUUID().toString());
        record.setOwnerUserId(arguments.getUserId());
        record.setReleaseId(arguments.getReleaseId());
        record.setState(arguments.getBieState().name());
        record.setVersion(arguments.getVersion());
        record.setStatus(arguments.getStatus());
        record.setLastUpdatedBy(arguments.getUserId());
        record.setLastUpdateTimestamp(arguments.getTimestamp());

        dslContext.insertInto(TOP_LEVEL_ASBIEP)
                .set(record)
                .execute();
        return record.getTopLevelAsbiepId();
    }

    public class InsertAbieArguments {
        private String userId;
        private String accManifestId;
        private String path;
        private String topLevelAsbiepId;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertAbieArguments setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public InsertAbieArguments setAccManifestId(String accManifestId) {
            this.accManifestId = accManifestId;
            return this;
        }

        public String getPath() {
            return path;
        }

        public InsertAbieArguments setPath(String path) {
            this.path = path;
            return this;
        }

        public InsertAbieArguments setTopLevelAsbiepId(String topLevelAsbiepId) {
            this.topLevelAsbiepId = topLevelAsbiepId;
            return this;
        }

        public InsertAbieArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public InsertAbieArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public InsertAbieArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public String getUserId() {
            return userId;
        }

        public String getAccManifestId() {
            return accManifestId;
        }

        public String getTopLevelAsbiepId() {
            return topLevelAsbiepId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String execute() {
            return insertAbie(this);
        }
    }

    public InsertAbieArguments insertAbie() {
        return new InsertAbieArguments();
    }

    private String insertAbie(InsertAbieArguments arguments) {
        String abieId = UUID.randomUUID().toString();
        dslContext.insertInto(ABIE)
                .set(ABIE.ABIE_ID, abieId)
                .set(ABIE.GUID, ScoreGuid.randomGuid())
                .set(ABIE.BASED_ACC_MANIFEST_ID, arguments.getAccManifestId())
                .set(ABIE.PATH, arguments.getPath())
                .set(ABIE.HASH_PATH, sha256(arguments.getPath()))
                .set(ABIE.CREATED_BY, arguments.getUserId())
                .set(ABIE.LAST_UPDATED_BY, arguments.getUserId())
                .set(ABIE.CREATION_TIMESTAMP, arguments.getTimestamp())
                .set(ABIE.LAST_UPDATE_TIMESTAMP, arguments.getTimestamp())
                .set(ABIE.OWNER_TOP_LEVEL_ASBIEP_ID, arguments.getTopLevelAsbiepId())
                .execute();
        return abieId;
    }

    public class InsertBizCtxAssignmentArguments {
        private String topLevelAsbiepId;
        private List<String> bizCtxIds = Collections.emptyList();

        public InsertBizCtxAssignmentArguments setTopLevelAsbiepId(String topLevelAsbiepId) {
            this.topLevelAsbiepId = topLevelAsbiepId;
            return this;
        }

        public InsertBizCtxAssignmentArguments setBizCtxIds(List<String> bizCtxIds) {
            if (bizCtxIds != null && !bizCtxIds.isEmpty()) {
                this.bizCtxIds = bizCtxIds;
            }
            return this;
        }

        public String getTopLevelAsbiepId() {
            return topLevelAsbiepId;
        }

        public List<String> getBizCtxIds() {
            return bizCtxIds;
        }

        public void execute() {
            insertBizCtxAssignments(this);
        }
    }

    public InsertBizCtxAssignmentArguments insertBizCtxAssignments() {
        return new InsertBizCtxAssignmentArguments();
    }

    private void insertBizCtxAssignments(InsertBizCtxAssignmentArguments arguments) {
        dslContext.batch(
                arguments.getBizCtxIds().stream().map(bizCtxId -> {
                    return dslContext.insertInto(BIZ_CTX_ASSIGNMENT)
                            .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ASSIGNMENT_ID, UUID.randomUUID().toString())
                            .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID, arguments.topLevelAsbiepId)
                            .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, bizCtxId);
                }).collect(Collectors.toList())
        ).execute();
    }

    public class InsertAsbiepArguments {
        private String asccpManifestId;
        private String roleOfAbieId;
        private String topLevelAsbiepId;
        private String path;
        private String userId;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertAsbiepArguments setAsccpManifestId(String asccpManifestId) {
            this.asccpManifestId = asccpManifestId;
            return this;
        }

        public InsertAsbiepArguments setRoleOfAbieId(String roleOfAbieId) {
            this.roleOfAbieId = roleOfAbieId;
            return this;
        }

        public InsertAsbiepArguments setTopLevelAsbiepId(String topLevelAsbiepId) {
            this.topLevelAsbiepId = topLevelAsbiepId;
            return this;
        }

        public String getPath() {
            return path;
        }

        public InsertAsbiepArguments setPath(String path) {
            this.path = path;
            return this;
        }

        public InsertAsbiepArguments setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public InsertAsbiepArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public InsertAsbiepArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public InsertAsbiepArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public String getAsccpManifestId() {
            return asccpManifestId;
        }

        public String getRoleOfAbieId() {
            return roleOfAbieId;
        }

        public String getTopLevelAsbiepId() {
            return topLevelAsbiepId;
        }

        public String getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String execute() {
            return insertAsbiep(this);
        }
    }

    public InsertAsbiepArguments insertAsbiep() {
        return new InsertAsbiepArguments();
    }

    private String insertAsbiep(InsertAsbiepArguments arguments) {
        String asbiepId = UUID.randomUUID().toString();
        dslContext.insertInto(ASBIEP)
                .set(ASBIEP.ASBIEP_ID, asbiepId)
                .set(ASBIEP.GUID, ScoreGuid.randomGuid())
                .set(ASBIEP.BASED_ASCCP_MANIFEST_ID, arguments.getAsccpManifestId())
                .set(ASBIEP.PATH, arguments.getPath())
                .set(ASBIEP.HASH_PATH, sha256(arguments.getPath()))
                .set(ASBIEP.ROLE_OF_ABIE_ID, arguments.getRoleOfAbieId())
                .set(ASBIEP.CREATED_BY, arguments.getUserId())
                .set(ASBIEP.LAST_UPDATED_BY, arguments.getUserId())
                .set(ASBIEP.CREATION_TIMESTAMP, arguments.getTimestamp())
                .set(ASBIEP.LAST_UPDATE_TIMESTAMP, arguments.getTimestamp())
                .set(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID, arguments.getTopLevelAsbiepId())
                .execute();
        return asbiepId;
    }

    public class UpdateTopLevelAsbiepArguments {
        private String asbiepId;
        private String topLevelAsbiepId;

        public UpdateTopLevelAsbiepArguments setAsbiepId(String asbiepId) {
            this.asbiepId = asbiepId;
            return this;
        }

        public UpdateTopLevelAsbiepArguments setTopLevelAsbiepId(String topLevelAsbiepId) {
            this.topLevelAsbiepId = topLevelAsbiepId;
            return this;
        }

        public String getAsbiepId() {
            return asbiepId;
        }

        public String getTopLevelAsbiepId() {
            return topLevelAsbiepId;
        }

        public void execute() {
            updateTopLevelAsbiep(this);
        }
    }

    public UpdateTopLevelAsbiepArguments updateTopLevelAsbiep() {
        return new UpdateTopLevelAsbiepArguments();
    }

    private void updateTopLevelAsbiep(UpdateTopLevelAsbiepArguments arguments) {
        dslContext.update(TOP_LEVEL_ASBIEP)
                .set(TOP_LEVEL_ASBIEP.ASBIEP_ID, arguments.getAsbiepId())
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(arguments.getTopLevelAsbiepId()))
                .execute();
    }

    public class SelectBieListArguments {

        private final List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        private String den;
        private String type;

        public SelectBieListArguments setDen(String den) {
            if (StringUtils.hasLength(den)) {
                conditions.addAll(contains(den, ASCCP.DEN));
            }
            return this;
        }

        public SelectBieListArguments setPropertyTerm(String propertyTerm) {
            if (StringUtils.hasLength(propertyTerm)) {
                conditions.addAll(contains(propertyTerm, ASCCP.PROPERTY_TERM));
            }
            return this;
        }

        public SelectBieListArguments setBusinessContext(String businessContext) {
            if (StringUtils.hasLength(businessContext)) {
                conditions.addAll(contains(businessContext, BIZ_CTX.NAME));
            }
            return this;
        }

        public SelectBieListArguments setAsccpManifestId(String asccpManifestId) {
            if (StringUtils.hasLength(asccpManifestId)) {
                conditions.add(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(asccpManifestId));
            }
            return this;
        }

        public SelectBieListArguments setExcludePropertyTerms(List<String> excludePropertyTerms) {
            if (!excludePropertyTerms.isEmpty()) {
                conditions.add(ASCCP.PROPERTY_TERM.notIn(excludePropertyTerms));
            }
            return this;
        }

        public SelectBieListArguments setExcludeTopLevelAsbiepIds(List<String> excludeTopLevelAsbiepIds) {
            if (!excludeTopLevelAsbiepIds.isEmpty()) {
                conditions.add(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.notIn(excludeTopLevelAsbiepIds));
            }
            return this;
        }

        public SelectBieListArguments setIncludeTopLevelAsbiepIds(List<String> includeTopLevelAsbiepIds) {
            if (!includeTopLevelAsbiepIds.isEmpty()) {
                conditions.add(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.in(includeTopLevelAsbiepIds));
            }
            return this;
        }

        public SelectBieListArguments setStates(List<BieState> states) {
            if (!states.isEmpty()) {
                conditions.add(TOP_LEVEL_ASBIEP.STATE.in(states.stream().map(e -> e.name()).collect(Collectors.toList())));
            }
            return this;
        }

        public SelectBieListArguments setBieIdAndType(String bieId, List<String> types) {
            if (types.size() == 1) {
                String type = types.get(0);
                if (type.equals("ASBIE")) {
                    conditions.add(ASBIE.ASBIE_ID.eq(bieId));
                } else if (type.equals("BBIE")) {
                    conditions.add(BBIE.BBIE_ID.eq(bieId));
                }
            }
            return this;
        }

        public SelectBieListArguments setAsccBccDen(String den) {
            this.den = den;
            return this;
        }

        public SelectBieListArguments setType(String type) {
            this.type = type;
            return this;
        }

        public SelectBieListArguments setOwnerLoginIds(List<String> ownerLoginIds) {
            if (!ownerLoginIds.isEmpty()) {
                conditions.add(APP_USER.LOGIN_ID.in(ownerLoginIds));
            }
            return this;
        }

        public SelectBieListArguments setUpdaterLoginIds(List<String> updaterLoginIds) {
            if (!updaterLoginIds.isEmpty()) {
                conditions.add(APP_USER.as("updater").LOGIN_ID.in(updaterLoginIds));
            }
            return this;
        }

        public SelectBieListArguments setUpdateDate(Date from, Date to) {
            return setUpdateDate(
                    (from != null) ? new Timestamp(from.getTime()).toLocalDateTime() : null,
                    (to != null) ? new Timestamp(to.getTime()).toLocalDateTime() : null
            );
        }

        public SelectBieListArguments setUpdateDate(LocalDateTime from, LocalDateTime to) {
            if (from != null) {
                conditions.add(TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP.greaterOrEqual(from));
            }
            if (to != null) {
                conditions.add(TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP.lessThan(to));
            }
            return this;
        }

        public SelectBieListArguments setAccess(String userId, AccessPrivilege access) {
            if (access != null) {
                switch (access) {
                    case CanEdit:
                        conditions.add(
                                and(
                                        TOP_LEVEL_ASBIEP.STATE.notEqual(Initiating.name()),
                                        TOP_LEVEL_ASBIEP.OWNER_USER_ID.eq(userId)
                                )
                        );
                        break;

                    case CanView:
                        conditions.add(
                                or(
                                        TOP_LEVEL_ASBIEP.STATE.in(QA.name(), Production.name()),
                                        and(
                                                TOP_LEVEL_ASBIEP.STATE.notEqual(Initiating.name()),
                                                TOP_LEVEL_ASBIEP.OWNER_USER_ID.eq(userId)
                                        )
                                )
                        );
                        break;
                }
            }
            return this;
        }

        public SelectBieListArguments setSort(String field, String direction) {
            if (StringUtils.hasLength(field)) {
                switch (field) {
                    case "state":
                        if ("asc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ASBIEP.STATE.asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ASBIEP.STATE.desc();
                        }

                        break;

                    case "topLevelAsccpPropertyTerm":
                        if ("asc".equals(direction)) {
                            this.sortField = field("topLevelAsccpPropertyTerm").asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = field("topLevelAsccpPropertyTerm").desc();
                        }

                        break;

                    case "den":
                        if ("asc".equals(direction)) {
                            this.sortField = field("den").asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = field("den").desc();
                        }
                        break;

                    case "lastUpdateTimestamp":
                        if ("asc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP.asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP.desc();
                        }

                        break;
                }
            }

            return this;
        }

        public SelectBieListArguments setOffset(int offset, int numberOfRows) {
            this.offset = offset;
            this.numberOfRows = numberOfRows;
            return this;
        }

        public SelectBieListArguments setReleaseId(String releaseId) {
            if (StringUtils.hasLength(releaseId)) {
                conditions.add(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(releaseId));
            }
            return this;
        }

        public SelectBieListArguments setOwnedByDeveloper(Boolean ownedByDeveloper) {
            if (ownedByDeveloper != null) {
                conditions.add(APP_USER.IS_DEVELOPER.eq(ownedByDeveloper ? (byte) 1 : 0));
            }
            return this;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public SortField getSortField() {
            return sortField;
        }

        public int getOffset() {
            return offset;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }

        public String getDen() {
            return den;
        }

        public String getType() {
            return type;
        }

        public <E> PaginationResponse<E> fetchInto(Class<? extends E> type) {
            return selectBieList(this, type);
        }

        public <E> PaginationResponse<E> fetchAsbieBbieInto(List<String> types, Class<? extends E> type) {
            return selectAsbieBbieList(this, types, type);
        }
    }

    public SelectBieListArguments selectBieLists() {
        return new SelectBieListArguments();
    }

    private SelectOnConditionStep<Record14<
            String, String, String, String, String,
            String, String, String, String, String,
            String, LocalDateTime, String, String>> getSelectOnConditionStep() {
        return dslContext.selectDistinct(
                        TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID,
                        TOP_LEVEL_ASBIEP.VERSION,
                        TOP_LEVEL_ASBIEP.STATUS,
                        ABIE.GUID,
                        ASCCP.DEN,
                        ASCCP.PROPERTY_TERM,
                        RELEASE.RELEASE_NUM,
                        TOP_LEVEL_ASBIEP.OWNER_USER_ID,
                        APP_USER.LOGIN_ID.as("owner"),
                        ASBIEP.BIZ_TERM,
                        ASBIEP.REMARK,
                        TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP,
                        APP_USER.as("updater").LOGIN_ID.as("last_update_user"),
                        TOP_LEVEL_ASBIEP.STATE)
                .from(TOP_LEVEL_ASBIEP)
                .join(ASBIEP).on(and(
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID),
                        ASBIEP.ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.ASBIEP_ID))
                )
                .join(ABIE).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(APP_USER).on(APP_USER.APP_USER_ID.eq(TOP_LEVEL_ASBIEP.OWNER_USER_ID))
                .join(APP_USER.as("updater")).on(APP_USER.as("updater").APP_USER_ID.eq(TOP_LEVEL_ASBIEP.LAST_UPDATED_BY))
                .join(RELEASE).on(RELEASE.RELEASE_ID.eq(TOP_LEVEL_ASBIEP.RELEASE_ID))
                .join(BIZ_CTX_ASSIGNMENT).on(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID))
                .join(BIZ_CTX).on(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(BIZ_CTX.BIZ_CTX_ID));
    }

    private <E> PaginationResponse<E> selectBieList(SelectBieListArguments arguments, Class<? extends E> type) {
        SelectOnConditionStep<Record14<
                String, String, String, String, String,
                String, String, String, String, String,
                String, LocalDateTime, String, String>> step = getSelectOnConditionStep();

        SelectConnectByStep<Record14<
                String, String, String, String, String,
                String, String, String, String, String,
                String, LocalDateTime, String, String>> conditionStep = step.where(arguments.getConditions());

        int pageCount = dslContext.fetchCount(conditionStep);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep<Record14<
                String, String, String, String, String,
                String, String, String, String, String,
                String, LocalDateTime, String, String>> offsetStep = null;
        if (sortField != null) {
            if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                offsetStep = conditionStep.orderBy(sortField)
                        .limit(arguments.getOffset(), arguments.getNumberOfRows());
            }
        } else {
            if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                offsetStep = conditionStep
                        .limit(arguments.getOffset(), arguments.getNumberOfRows());
            }
        }

        return new PaginationResponse<>(pageCount,
                (offsetStep != null) ?
                        offsetStep.fetchInto(type) : conditionStep.fetchInto(type));
    }

    public String getAsccpManifestIdByTopLevelAsbiepId(String topLevelAsbiepId) {
        return dslContext.select(ASBIEP.BASED_ASCCP_MANIFEST_ID)
                .from(ASBIEP)
                .join(TOP_LEVEL_ASBIEP).on(and(
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID),
                        ASBIEP.ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.ASBIEP_ID)
                ))
                .where(and(
                        TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                ))
                .fetchOptionalInto(String.class).orElse(null);
    }

    public AsccpManifestRecord getAsccpManifestIdByTopLevelAsbiepIdAndReleaseId(String topLevelAsbiepId, String releaseId) {
        String asccpId = dslContext.select(ASCCP_MANIFEST.ASCCP_ID)
                .from(ASBIEP)
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(TOP_LEVEL_ASBIEP).on(and(
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID),
                        ASBIEP.ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.ASBIEP_ID)
                ))
                .where(and(
                        TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                ))
                .fetchOptionalInto(String.class).orElse(null);

        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(and(ASCCP_MANIFEST.ASCCP_ID.eq(asccpId),
                        ASCCP_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetchOptionalInto(AsccpManifestRecord.class).orElse(null);
    }

    public List<String> getReusingTopLevelAsbiepIds(String reusedTopLevelAsbiepId) {
        return dslContext.select(ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID)
                .from(ASBIE)
                .join(ASBIEP).on(ASBIE.TO_ASBIEP_ID.eq(ASBIEP.ASBIEP_ID))
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.notEqual(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID),
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(reusedTopLevelAsbiepId)
                ))
                .fetchInto(String.class);
    }

    public SelectOrderByStep getAsbieList(SelectBieListArguments arguments) {
        List<Condition> conditions = arguments.getConditions().stream().collect(Collectors.toList());
        ;
        if (arguments.getDen() != null && StringUtils.hasLength(arguments.getDen())) {
            conditions.add(ASCC.DEN.contains(arguments.getDen()));
        }
        return dslContext.select(
                        inline("ASBIE").as("type"),
                        ASBIE.ASBIE_ID.as("bieId"),
                        ASBIE.GUID,
                        ASCC.DEN,
                        TOP_LEVEL_ASBIEP.STATE,
                        TOP_LEVEL_ASBIEP.VERSION,
                        TOP_LEVEL_ASBIEP.STATUS,
                        BIZ_CTX.NAME.as("bizCtxName"),
                        RELEASE.RELEASE_ID,
                        RELEASE.RELEASE_NUM,
                        ASBIE.REMARK,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdateUser"),
                        APP_USER.LOGIN_ID.as("owner"),
                        ASBIE.LAST_UPDATE_TIMESTAMP,
                        ASBIE.IS_USED.as("used"),
                        TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID,
                        ASCCP.PROPERTY_TERM.as("topLevelAsccpPropertyTerm"))
                .from(ASBIE)
//                next two joins to get DEN
                .join(ASCC_MANIFEST).on(ASBIE.BASED_ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                .join(ASCC).on(ASCC_MANIFEST.ASCC_ID.eq(ASCC.ASCC_ID))
//                join with TOP_LEVEL_ASBIEP to get state, version, status
                .join(TOP_LEVEL_ASBIEP).on(and(
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID)
                ))
//                next three joins to get top level property term
                .join(ASBIEP).on(TOP_LEVEL_ASBIEP.ASBIEP_ID.eq(ASBIEP.ASBIEP_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
//                join w RELEASE to get RELEASE_NUM
                .join(RELEASE).on(RELEASE.RELEASE_ID.eq(TOP_LEVEL_ASBIEP.RELEASE_ID))
//                next two joins to get BIZ_CTX.NAME
                .join(BIZ_CTX_ASSIGNMENT).on(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID))
                .join(BIZ_CTX).on(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(BIZ_CTX.BIZ_CTX_ID))
//                join with APP_USER to get updater and owner
                .join(APP_USER.as("appUserUpdater"))
                .on(ASBIE.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(ASBIE.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    public SelectOrderByStep getBbieList(SelectBieListArguments arguments) {
        List<Condition> conditions = arguments.getConditions().stream().collect(Collectors.toList());
        if (arguments.getDen() != null && StringUtils.hasLength(arguments.getDen())) {
            conditions.add(BCC.DEN.contains(arguments.getDen()));
        }
        return dslContext.select(
                        inline("BBIE").as("type"),
                        BBIE.BBIE_ID.as("bieId"),
                        BBIE.GUID,
                        BCC.DEN,
                        TOP_LEVEL_ASBIEP.STATE,
                        TOP_LEVEL_ASBIEP.VERSION,
                        TOP_LEVEL_ASBIEP.STATUS,
                        BIZ_CTX.NAME.as("bizCtxName"),
                        RELEASE.RELEASE_ID,
                        RELEASE.RELEASE_NUM,
                        BBIE.REMARK,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdateUser"),
                        APP_USER.LOGIN_ID.as("owner"),
                        BBIE.LAST_UPDATE_TIMESTAMP,
                        BBIE.IS_USED.as("used"),
                        TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID,
                        ASCCP.PROPERTY_TERM.as("topLevelAsccpPropertyTerm"))
                .from(BBIE)
                //                next two joins to get DEN
                .join(BCC_MANIFEST).on(BBIE.BASED_BCC_MANIFEST_ID.eq(BCC_MANIFEST.BCC_MANIFEST_ID))
                .join(BCC).on(BCC_MANIFEST.BCC_ID.eq(BCC.BCC_ID))
                //                join with TOP_LEVEL_ASBIEP to get state, version, status
                .join(TOP_LEVEL_ASBIEP).on(and(
                        BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID)
                ))
                //                next three joins to get top level property term
                .join(ASBIEP).on(TOP_LEVEL_ASBIEP.ASBIEP_ID.eq(ASBIEP.ASBIEP_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                //                join w RELEASE to get RELEASE_NUM
                .join(RELEASE).on(RELEASE.RELEASE_ID.eq(TOP_LEVEL_ASBIEP.RELEASE_ID))
                //                next two joins to get BIZ_CTX.NAME
                .join(BIZ_CTX_ASSIGNMENT).on(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID))
                .join(BIZ_CTX).on(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(BIZ_CTX.BIZ_CTX_ID))
                //                join with APP_USER to get updater
                .join(APP_USER.as("appUserUpdater"))
                .on(BBIE.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(BBIE.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    private <E> PaginationResponse<E> selectAsbieBbieList(SelectBieListArguments arguments, List<String> types, Class<? extends E> type) {

        SelectOrderByStep select = null;
        if (types.contains("ASBIE")) {
            select = getAsbieList(arguments);
        }
        if (types.contains("BBIE")) {
            select = (select != null) ? select.union(getBbieList(arguments)) :
                    getBbieList(arguments);
        }

        int pageCount = dslContext.fetchCount(select);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep<Record17<String, ULong, String, String,
                String, String, String, String, ULong, String, String, String,
                String, LocalDateTime, Byte, ULong, String>> offsetStep = null;
        if (sortField != null) {
            if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                offsetStep = select.orderBy(sortField).limit(arguments.getOffset(), arguments.getNumberOfRows());
            }
        } else {
            if (arguments.getOffset() >= 0 && arguments.getNumberOfRows() >= 0) {
                offsetStep = select.limit(arguments.getOffset(), arguments.getNumberOfRows());
            }
        }

        return new PaginationResponse<>(pageCount,
                (offsetStep != null) ?
                        offsetStep.fetchInto(type) : select.fetchInto(type));
    }

}
