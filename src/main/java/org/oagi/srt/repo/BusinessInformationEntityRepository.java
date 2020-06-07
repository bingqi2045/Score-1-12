package org.oagi.srt.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.entity.jooq.tables.records.TopLevelAbieRecord;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.or;
import static org.oagi.srt.data.BieState.*;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Repository
public class BusinessInformationEntityRepository {

    @Autowired
    private DSLContext dslContext;

    public class InsertTopLevelAbieArguments {
        private ULong releaseId;
        private ULong userId;
        private BieState bieState = WIP;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertTopLevelAbieArguments setReleaseId(BigInteger releaseId) {
            return setReleaseId(ULong.valueOf(releaseId));
        }

        public InsertTopLevelAbieArguments setReleaseId(ULong releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public InsertTopLevelAbieArguments setBieState(BieState bieState) {
            this.bieState = bieState;
            return this;
        }

        public InsertTopLevelAbieArguments setUserId(BigInteger userId) {
            return setUserId(ULong.valueOf(userId));
        }

        public InsertTopLevelAbieArguments setUserId(ULong userId) {
            this.userId = userId;
            return this;
        }

        public InsertTopLevelAbieArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public InsertTopLevelAbieArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public InsertTopLevelAbieArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ULong getReleaseId() {
            return releaseId;
        }

        public BieState getBieState() {
            return bieState;
        }

        public ULong getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public ULong execute() {
            return insertTopLevelAbie(this);
        }
    }

    public InsertTopLevelAbieArguments insertTopLevelAbie() {
        return new InsertTopLevelAbieArguments();
    }

    private ULong insertTopLevelAbie(InsertTopLevelAbieArguments arguments) {
        TopLevelAbieRecord record = new TopLevelAbieRecord();
        record.setOwnerUserId(arguments.getUserId());
        record.setReleaseId(arguments.getReleaseId());
        record.setState(arguments.getBieState().getValue());
        record.setLastUpdatedBy(arguments.getUserId());
        record.setLastUpdateTimestamp(arguments.getTimestamp());

        return dslContext.insertInto(TOP_LEVEL_ABIE)
                .set(record)
                .returningResult(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID)
                .fetchOne().value1();
    }

    public class InsertAbieArguments {
        private ULong userId;
        private ULong accManifestId;
        private String hashPath;
        private ULong topLevelAbieId;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertAbieArguments setUserId(BigInteger userId) {
            return setUserId(ULong.valueOf(userId));
        }

        public InsertAbieArguments setUserId(ULong userId) {
            this.userId = userId;
            return this;
        }

        public InsertAbieArguments setAccManifestId(BigInteger accManifestId) {
            return setAccManifestId(ULong.valueOf(accManifestId));
        }

        public InsertAbieArguments setAccManifestId(ULong accManifestId) {
            this.accManifestId = accManifestId;
            return this;
        }

        public String getHashPath() {
            return hashPath;
        }

        public InsertAbieArguments setHashPath(String hashPath) {
            this.hashPath = hashPath;
            return this;
        }

        public InsertAbieArguments setTopLevelAbieId(BigInteger topLevelAbieId) {
            return setTopLevelAbieId(ULong.valueOf(topLevelAbieId));
        }

        public InsertAbieArguments setTopLevelAbieId(ULong topLevelAbieId) {
            this.topLevelAbieId = topLevelAbieId;
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

        public ULong getUserId() {
            return userId;
        }

        public ULong getAccManifestId() {
            return accManifestId;
        }

        public ULong getTopLevelAbieId() {
            return topLevelAbieId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public ULong execute() {
            return insertAbie(this);
        }
    }

    public InsertAbieArguments insertAbie() {
        return new InsertAbieArguments();
    }

    private ULong insertAbie(InsertAbieArguments arguments) {
        return dslContext.insertInto(ABIE)
                .set(ABIE.GUID, SrtGuid.randomGuid())
                .set(ABIE.BASED_ACC_MANIFEST_ID, arguments.getAccManifestId())
                .set(ABIE.HASH_PATH, arguments.getHashPath())
                .set(ABIE.CREATED_BY, arguments.getUserId())
                .set(ABIE.LAST_UPDATED_BY, arguments.getUserId())
                .set(ABIE.CREATION_TIMESTAMP, arguments.getTimestamp())
                .set(ABIE.LAST_UPDATE_TIMESTAMP, arguments.getTimestamp())
                .set(ABIE.STATE, WIP.getValue())
                .set(ABIE.OWNER_TOP_LEVEL_ABIE_ID, arguments.getTopLevelAbieId())
                .returningResult(ABIE.ABIE_ID)
                .fetchOne().value1();
    }

    public class InsertBizCtxAssignmentArguments {
        private ULong topLevelAbieId;
        private List<ULong> bizCtxIds = Collections.emptyList();

        public InsertBizCtxAssignmentArguments setTopLevelAbieId(BigInteger topLevelAbieId) {
            return setTopLevelAbieId(ULong.valueOf(topLevelAbieId));
        }

        public InsertBizCtxAssignmentArguments setTopLevelAbieId(ULong topLevelAbieId) {
            this.topLevelAbieId = topLevelAbieId;
            return this;
        }

        public InsertBizCtxAssignmentArguments setBizCtxIds(List<BigInteger> bizCtxIds) {
            if (bizCtxIds != null && !bizCtxIds.isEmpty()) {
                this.bizCtxIds = bizCtxIds.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList());
            }
            return this;
        }

        public ULong getTopLevelAbieId() {
            return topLevelAbieId;
        }

        public List<ULong> getBizCtxIds() {
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
                            .set(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID, arguments.topLevelAbieId)
                            .set(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID, bizCtxId);
                }).collect(Collectors.toList())
        ).execute();
    }

    public class InsertAsbiepArguments {
        private ULong asccpManifestId;
        private ULong roleOfAbieId;
        private ULong topLevelAbieId;
        private String hashPath;
        private ULong userId;
        private LocalDateTime timestamp = new Timestamp(System.currentTimeMillis()).toLocalDateTime();

        public InsertAsbiepArguments setAsccpManifestId(BigInteger asccpManifestId) {
            return setAsccpManifestId(ULong.valueOf(asccpManifestId));
        }

        public InsertAsbiepArguments setAsccpManifestId(ULong asccpManifestId) {
            this.asccpManifestId = asccpManifestId;
            return this;
        }

        public InsertAsbiepArguments setRoleOfAbieId(BigInteger roleOfAbieId) {
            return setRoleOfAbieId(ULong.valueOf(roleOfAbieId));
        }

        public InsertAsbiepArguments setRoleOfAbieId(ULong roleOfAbieId) {
            this.roleOfAbieId = roleOfAbieId;
            return this;
        }

        public InsertAsbiepArguments setTopLevelAbieId(BigInteger topLevelAbieId) {
            return setTopLevelAbieId(ULong.valueOf(topLevelAbieId));
        }

        public InsertAsbiepArguments setTopLevelAbieId(ULong topLevelAbieId) {
            this.topLevelAbieId = topLevelAbieId;
            return this;
        }

        public String getHashPath() {
            return hashPath;
        }

        public InsertAsbiepArguments setHashPath(String hashPath) {
            this.hashPath = hashPath;
            return this;
        }

        public InsertAsbiepArguments setUserId(BigInteger userId) {
            return setUserId(ULong.valueOf(userId));
        }

        public InsertAsbiepArguments setUserId(ULong userId) {
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

        public ULong getAsccpManifestId() {
            return asccpManifestId;
        }

        public ULong getRoleOfAbieId() {
            return roleOfAbieId;
        }

        public ULong getTopLevelAbieId() {
            return topLevelAbieId;
        }

        public ULong getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public ULong execute() {
            return insertAsbiep(this);
        }
    }

    public InsertAsbiepArguments insertAsbiep() {
        return new InsertAsbiepArguments();
    }

    private ULong insertAsbiep(InsertAsbiepArguments arguments) {
        return dslContext.insertInto(ASBIEP)
                .set(ASBIEP.GUID, SrtGuid.randomGuid())
                .set(ASBIEP.BASED_ASCCP_MANIFEST_ID, arguments.getAsccpManifestId())
                .set(ASBIEP.HASH_PATH, arguments.getHashPath())
                .set(ASBIEP.ROLE_OF_ABIE_ID, arguments.getRoleOfAbieId())
                .set(ASBIEP.CREATED_BY, arguments.getUserId())
                .set(ASBIEP.LAST_UPDATED_BY, arguments.getUserId())
                .set(ASBIEP.CREATION_TIMESTAMP, arguments.getTimestamp())
                .set(ASBIEP.LAST_UPDATE_TIMESTAMP, arguments.getTimestamp())
                .set(ASBIEP.OWNER_TOP_LEVEL_ABIE_ID, arguments.getTopLevelAbieId())
                .returningResult(ASBIEP.ASBIEP_ID)
                .fetchOne().value1();
    }

    public class UpdateTopLevelAbieArguments {
        private ULong abieId;
        private ULong topLevelAbieId;

        public UpdateTopLevelAbieArguments setAbieId(BigInteger abieId) {
            return setAbieId(ULong.valueOf(abieId));
        }

        public UpdateTopLevelAbieArguments setAbieId(ULong abieId) {
            this.abieId = abieId;
            return this;
        }

        public UpdateTopLevelAbieArguments setTopLevelAbieId(BigInteger topLevelAbieId) {
            return setTopLevelAbieId(ULong.valueOf(topLevelAbieId));
        }

        public UpdateTopLevelAbieArguments setTopLevelAbieId(ULong topLevelAbieId) {
            this.topLevelAbieId = topLevelAbieId;
            return this;
        }

        public ULong getAbieId() {
            return abieId;
        }

        public ULong getTopLevelAbieId() {
            return topLevelAbieId;
        }

        public void execute() {
            updateTopLevelAbie(this);
        }
    }

    public UpdateTopLevelAbieArguments updateTopLevelAbie() {
        return new UpdateTopLevelAbieArguments();
    }

    private void updateTopLevelAbie(UpdateTopLevelAbieArguments arguments) {
        dslContext.update(TOP_LEVEL_ABIE)
                .set(TOP_LEVEL_ABIE.ABIE_ID, arguments.getAbieId())
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(arguments.getTopLevelAbieId()))
                .execute();
    }

    public class SelectBieListArguments {

        private List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        public SelectBieListArguments setPropertyTerm(String propertyTerm) {
            if (!StringUtils.isEmpty(propertyTerm)) {
                conditions.addAll(contains(propertyTerm, ASCCP.PROPERTY_TERM));
            }
            return this;
        }

        public SelectBieListArguments setExcludes(List<String> excludes) {
            if (!excludes.isEmpty()) {
                conditions.add(ASCCP.PROPERTY_TERM.notIn(excludes));
            }
            return this;
        }

        public SelectBieListArguments setStates(List<BieState> states) {
            if (!states.isEmpty()) {
                conditions.add(ABIE.STATE.in(states.stream().map(e -> e.getValue()).collect(Collectors.toList())));
            }
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
                conditions.add(TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP.greaterOrEqual(from));
            }
            if (to != null) {
                conditions.add(TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP.lessThan(to));
            }
            return this;
        }

        public SelectBieListArguments setAccess(ULong userId, AccessPrivilege access) {
            if (access != null) {
                switch (access) {
                    case CanEdit:
                        conditions.add(
                                and(
                                        ABIE.STATE.notEqual(Initiating.getValue()),
                                        TOP_LEVEL_ABIE.OWNER_USER_ID.eq(userId)
                                )
                        );
                        break;

                    case CanView:
                        conditions.add(
                                or(
                                        ABIE.STATE.in(QA.getValue(), Production.getValue()),
                                        and(
                                                ABIE.STATE.notEqual(Initiating.getValue()),
                                                TOP_LEVEL_ABIE.OWNER_USER_ID.eq(userId)
                                        )
                                )
                        );
                        break;
                }
            }
            return this;
        }

        public SelectBieListArguments setSort(String field, String direction) {
            if (!StringUtils.isEmpty(field)) {
                switch (field) {
                    case "propertyTerm":
                        if ("asc".equals(direction)) {
                            this.sortField = ASCCP.PROPERTY_TERM.asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = ASCCP.PROPERTY_TERM.desc();
                        }

                        break;

                    case "releaseNum":
                        if ("asc".equals(direction)) {
                            this.sortField = RELEASE.RELEASE_NUM.asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = RELEASE.RELEASE_NUM.desc();
                        }

                        break;

                    case "lastUpdateTimestamp":
                        if ("asc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP.asc();
                        } else if ("desc".equals(direction)) {
                            this.sortField = TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP.desc();
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

        public <E> PaginationResponse<E> fetchInto(Class<? extends E> type) {
            return selectBieList(this, type);
        }
    }

    public SelectBieListArguments selectBieLists() {
        return new SelectBieListArguments();
    }

    private SelectOnConditionStep<Record11<
            ULong, String, String, String,
            ULong, String, String, String,
            LocalDateTime, String, Integer>> getSelectOnConditionStep() {
        return dslContext.select(
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                ABIE.GUID,
                ASCCP.PROPERTY_TERM,
                RELEASE.RELEASE_NUM,
                TOP_LEVEL_ABIE.OWNER_USER_ID,
                APP_USER.LOGIN_ID.as("owner"),
                ABIE.VERSION,
                ABIE.STATUS,
                TOP_LEVEL_ABIE.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("updater").LOGIN_ID.as("last_update_user"),
                ABIE.STATE.as("raw_state"))
                .from(TOP_LEVEL_ABIE)
                .join(ABIE).on(TOP_LEVEL_ABIE.ABIE_ID.eq(ABIE.ABIE_ID))
                .and(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ABIE.OWNER_TOP_LEVEL_ABIE_ID))
                .join(ASBIEP).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(APP_USER).on(APP_USER.APP_USER_ID.eq(TOP_LEVEL_ABIE.OWNER_USER_ID))
                .join(APP_USER.as("updater")).on(APP_USER.as("updater").APP_USER_ID.eq(TOP_LEVEL_ABIE.LAST_UPDATED_BY))
                .join(RELEASE).on(RELEASE.RELEASE_ID.eq(TOP_LEVEL_ABIE.RELEASE_ID));
    }

    private <E> PaginationResponse<E> selectBieList(SelectBieListArguments arguments, Class<? extends E> type) {
        SelectOnConditionStep<Record11<
                ULong, String, String, String,
                ULong, String, String, String,
                LocalDateTime, String, Integer>> step = getSelectOnConditionStep();

        SelectConnectByStep<Record11<
                ULong, String, String, String,
                ULong, String, String, String,
                LocalDateTime, String, Integer>> conditionStep = step.where(arguments.getConditions());

        int pageCount = dslContext.fetchCount(conditionStep);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep<Record11<
                ULong, String, String, String,
                ULong, String, String, String,
                LocalDateTime, String, Integer>> offsetStep = null;
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

    public BigInteger getAsccpManifestIdByTopLevelAbieId(BigInteger topLevelAbieId) {
        return dslContext.select(ASBIEP.BASED_ASCCP_MANIFEST_ID)
                .from(ASBIEP)
                .join(ABIE).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(TOP_LEVEL_ABIE).on(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID),
                        ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID)
                ))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOptionalInto(BigInteger.class).orElse(null);
    }

}
