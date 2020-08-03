package org.oagi.srt.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
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

import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Repository
public class BusinessContextRepository {

    private DSLContext dslContext;

    public BusinessContextRepository(@Autowired DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public class SelectBusinessContextArguments {

        private ULong topLevelAsbiepId;
        private List<ULong> bizCtxIds = new ArrayList();
        private List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        public SelectBusinessContextArguments setTopLevelAsbiepId(BigInteger topLevelAsbiepId) {
            if (topLevelAsbiepId != null) {
                return setTopLevelAsbiepId(ULong.valueOf(topLevelAsbiepId));
            }
            return this;
        }

        public SelectBusinessContextArguments setTopLevelAsbiepId(ULong topLevelAsbiepId) {
            this.topLevelAsbiepId = topLevelAsbiepId;
            return this;
        }

        public SelectBusinessContextArguments setName(String name) {
            if (!StringUtils.isEmpty(name)) {
                conditions.addAll(contains(name, BIZ_CTX.NAME));
            }
            return this;
        }

        public SelectBusinessContextArguments setBizCtxIds(List<ULong> bizCtxIds) {
            this.bizCtxIds.addAll(bizCtxIds);
            return this;
        }

        public SelectBusinessContextArguments setUpdaterIds(List<ULong> updaterIds) {
            if (!updaterIds.isEmpty()) {
                conditions.add(APP_USER.LOGIN_ID.in(updaterIds));
            }
            return this;
        }

        public SelectBusinessContextArguments setUpdateDate(Date from, Date to) {
            return setUpdateDate(
                    (from != null) ? new Timestamp(from.getTime()).toLocalDateTime() : null,
                    (to != null) ? new Timestamp(to.getTime()).toLocalDateTime() : null
            );
        }

        public SelectBusinessContextArguments setUpdateDate(LocalDateTime from, LocalDateTime to) {
            if (from != null) {
                conditions.add(BIZ_CTX.LAST_UPDATE_TIMESTAMP.greaterOrEqual(from));
            }
            if (to != null) {
                conditions.add(BIZ_CTX.LAST_UPDATE_TIMESTAMP.lessThan(to));
            }
            return this;
        }

        public SelectBusinessContextArguments setSort(String field, String direction) {
            if (!StringUtils.isEmpty(field)) {
                switch (field) {
                    case "name":
                        if ("asc".equals(direction)) {
                            sortField = BIZ_CTX.NAME.asc();
                        } else if ("desc".equals(direction)) {
                            sortField = BIZ_CTX.NAME.desc();
                        }

                        break;

                    case "lastUpdateTimestamp":
                        if ("asc".equals(direction)) {
                            sortField = BIZ_CTX.LAST_UPDATE_TIMESTAMP.asc();
                        } else if ("desc".equals(direction)) {
                            sortField = BIZ_CTX.LAST_UPDATE_TIMESTAMP.desc();
                        }

                        break;
                }
            }

            return this;
        }

        public SelectBusinessContextArguments setOffset(int offset, int numberOfRows) {
            this.offset = offset;
            this.numberOfRows = numberOfRows;
            return this;
        }

        public List<Condition> getConditions() {
            if (topLevelAsbiepId != null) {
                List<ULong> bizCtxIds = dslContext.selectDistinct(
                        BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                        .from(BIZ_CTX_ASSIGNMENT)
                        .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId))
                        .fetchInto(ULong.class);

                setBizCtxIds(bizCtxIds);
            }

            if (!bizCtxIds.isEmpty()) {
                conditions.add(BIZ_CTX.BIZ_CTX_ID.in(bizCtxIds));
            }

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
            return selectBusinessContexts(this, type);
        }
    }

    public SelectBusinessContextArguments selectBusinessContexts() {
        return new SelectBusinessContextArguments();
    }

    private SelectOnConditionStep
            <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> getSelectOnConditionStepForBusinessContext() {
        return dslContext.select(
                BIZ_CTX.BIZ_CTX_ID,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.CREATION_TIMESTAMP,
                APP_USER.LOGIN_ID.as("last_update_user"),
                BIZ_CTX.LAST_UPDATE_TIMESTAMP)
                .from(BIZ_CTX)
                .join(APP_USER).on(BIZ_CTX.LAST_UPDATED_BY.eq(APP_USER.APP_USER_ID));
    }

    private <E> PaginationResponse<E> selectBusinessContexts(SelectBusinessContextArguments arguments, Class<? extends E> type) {
        SelectOnConditionStep
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> step =
                getSelectOnConditionStepForBusinessContext();

        SelectConnectByStep
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> conditionStep =
                step.where(arguments.getConditions());

        int pageCount = dslContext.fetchCount(conditionStep);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> offsetStep = null;
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

    public BusinessContext findBusinessContextByBizCtxId(BigInteger bizCtxId) {
        if (bizCtxId.longValue() <= 0L) {
            return null;
        }

        BusinessContext bizCtx = getSelectOnConditionStepForBusinessContext()
                .where(BIZ_CTX.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOptionalInto(BusinessContext.class).orElse(null);
        if (bizCtx == null) {
            return null;
        }

        bizCtx.setBizCtxValues(findBusinessContextValuesByBizCtxId(bizCtxId));
        bizCtx.setUsed(used(bizCtxId));

        return bizCtx;
    }

    public boolean used(BigInteger bizCtxId) {
        if (bizCtxId.longValue() <= 0L) {
            return false;
        }
        return dslContext.selectCount()
                .from(BIZ_CTX_ASSIGNMENT)
                .where(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public List<BusinessContext> findBusinessContextsByBizCtxIdIn(List<BigInteger> bizCtxIds) {
        if (bizCtxIds == null || bizCtxIds.isEmpty()) {
            return Collections.emptyList();
        }

        return bizCtxIds.stream()
                .map(e -> findBusinessContextByBizCtxId(e))
                .collect(Collectors.toList());
    }

    private SelectOnConditionStep
            <Record9<ULong, ULong, ULong, String, ULong,
                    String, ULong, String, String>>
    getSelectOnConditionStepForBusinessContextValue() {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME.as("ctx_scheme_name"),
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE.as("ctx_scheme_value"),
                CTX_SCHEME_VALUE.MEANING.as("ctx_scheme_value_meaning")
        ).from(BIZ_CTX_VALUE)
                .join(CTX_SCHEME_VALUE).on(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.equal(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID))
                .join(CTX_SCHEME).on(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.equal(CTX_SCHEME.CTX_SCHEME_ID))
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID));
    }

    public List<BusinessContextValue> findBusinessContextValues() {
        return getSelectOnConditionStepForBusinessContextValue()
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> findBusinessContextValuesByBizCtxId(BigInteger bizCtxId) {
        if (bizCtxId.longValue() <= 0L) {
            return Collections.emptyList();
        }
        return getSelectOnConditionStepForBusinessContextValue()
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(BusinessContextValue.class);
    }

    public class SelectContextSchemeValueArguments {

        private List<Condition> conditions = new ArrayList();

        public SelectContextSchemeValueArguments setContextSchemeId(BigInteger contextSchemeId) {
            return setContextSchemeId(ULong.valueOf(contextSchemeId));
        }

        public SelectContextSchemeValueArguments setContextSchemeId(ULong contextSchemeId) {
            conditions.add(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(contextSchemeId));
            return this;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public <E> List<E> fetchInto(Class<E> type) {
            return selectContextSchemeValues(this, type);
        }
    }

    public SelectContextSchemeValueArguments selectContextSchemeValues() {
        return new SelectContextSchemeValueArguments();
    }

    private <E> List<E> selectContextSchemeValues(SelectContextSchemeValueArguments arguments, Class<E> type) {
        return dslContext.select(
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE,
                CTX_SCHEME_VALUE.MEANING)
                .from(CTX_SCHEME_VALUE)
                .where(arguments.getConditions())
                .fetchInto(type);
    }

    public class SelectBusinessContextValueArguments {

        private List<Condition> conditions = new ArrayList();

        public SelectBusinessContextValueArguments setBusinessContextId(BigInteger businessContextId) {
            return setBusinessContextId(ULong.valueOf(businessContextId));
        }

        public SelectBusinessContextValueArguments setBusinessContextId(ULong businessContextId) {
            conditions.add(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(businessContextId));
            return this;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public <E> List<E> fetchInto(Class<E> type) {
            return selectContextSchemeValues(this, type);
        }
    }

    public SelectBusinessContextValueArguments selectBusinessContextValues() {
        return new SelectBusinessContextValueArguments();
    }

    private <E> List<E> selectContextSchemeValues(SelectBusinessContextValueArguments arguments, Class<E> type) {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID)
                .from(BIZ_CTX_VALUE)
                .where(arguments.getConditions())
                .fetchInto(type);
    }

    public class InsertBusinessContextArguments {

        private String guid;

        private String name;

        private ULong userId;

        private LocalDateTime timestamp = LocalDateTime.now();

        public InsertBusinessContextArguments setGuid(String guid) {
            this.guid = guid;
            return this;
        }

        public InsertBusinessContextArguments setName(String name) {
            this.name = name;
            return this;
        }

        public InsertBusinessContextArguments setUserId(long userId) {
            return setUserId(ULong.valueOf(userId));
        }

        public InsertBusinessContextArguments setUserId(ULong userId) {
            this.userId = userId;
            return this;
        }

        public InsertBusinessContextArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public InsertBusinessContextArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public InsertBusinessContextArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public String getGuid() {
            return guid;
        }

        public String getName() {
            return name;
        }

        public ULong getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public ULong execute() {
            return insertBusinessContext(this);
        }
    }

    public InsertBusinessContextArguments insertBusinessContext() {
        return new InsertBusinessContextArguments();
    }

    private ULong insertBusinessContext(InsertBusinessContextArguments arguments) {
        return dslContext.insertInto(BIZ_CTX,
                BIZ_CTX.GUID,
                BIZ_CTX.NAME,
                BIZ_CTX.CREATED_BY,
                BIZ_CTX.LAST_UPDATED_BY,
                BIZ_CTX.CREATION_TIMESTAMP,
                BIZ_CTX.LAST_UPDATE_TIMESTAMP)
                .values(
                        arguments.getGuid(),
                        arguments.getName(),
                        arguments.getUserId(), arguments.getUserId(),
                        arguments.getTimestamp(), arguments.getTimestamp()
                )
                .returning(BIZ_CTX.BIZ_CTX_ID).fetchOne().getBizCtxId();
    }

    public class InsertBusinessContextValueArguments {

        private ULong businessContextId;

        private ULong contextSchemeValueId;

        public InsertBusinessContextValueArguments setBusinessContextId(BigInteger businessContextId) {
            return setBusinessContextId(ULong.valueOf(businessContextId));
        }

        public InsertBusinessContextValueArguments setBusinessContextId(ULong businessContextId) {
            this.businessContextId = businessContextId;
            return this;
        }

        public InsertBusinessContextValueArguments setContextSchemeValueId(BigInteger contextSchemeValueId) {
            return setContextSchemeValueId(ULong.valueOf(contextSchemeValueId));
        }

        public InsertBusinessContextValueArguments setContextSchemeValueId(ULong contextSchemeValueId) {
            this.contextSchemeValueId = contextSchemeValueId;
            return this;
        }

        public ULong getBusinessContextId() {
            return businessContextId;
        }

        public ULong getContextSchemeValueId() {
            return contextSchemeValueId;
        }

        public void execute() {
            insertBusinessContextValue(this);
        }
    }

    public InsertBusinessContextValueArguments insertBusinessContextValue() {
        return new InsertBusinessContextValueArguments();
    }

    private void insertBusinessContextValue(InsertBusinessContextValueArguments arguments) {
        dslContext.insertInto(BIZ_CTX_VALUE,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID)
                .values(
                        arguments.getBusinessContextId(),
                        arguments.getContextSchemeValueId()
                )
                .execute();
    }

    public class UpdateBusinessContextArguments {

        private ULong userId;

        private LocalDateTime timestamp;

        private String name;

        private ULong businessContextId;

        public UpdateBusinessContextArguments setUserId(BigInteger userId) {
            return setUserId(ULong.valueOf(userId));
        }

        public UpdateBusinessContextArguments setUserId(ULong userId) {
            this.userId = userId;
            return this;
        }

        public UpdateBusinessContextArguments setTimestamp(long millis) {
            return setTimestamp(new Timestamp(millis).toLocalDateTime());
        }

        public UpdateBusinessContextArguments setTimestamp(Date date) {
            return setTimestamp(new Timestamp(date.getTime()).toLocalDateTime());
        }

        public UpdateBusinessContextArguments setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public UpdateBusinessContextArguments setName(String name) {
            if (!StringUtils.isEmpty(name)) {
                this.name = name;
            }
            return this;
        }

        public UpdateBusinessContextArguments setBusinessContextId(BigInteger businessContextId) {
            return setBusinessContextId(ULong.valueOf(businessContextId));
        }

        public UpdateBusinessContextArguments setBusinessContextId(ULong businessContextId) {
            this.businessContextId = businessContextId;
            return this;
        }

        public ULong getUserId() {
            return userId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getName() {
            return name;
        }

        public ULong getBusinessContextId() {
            return businessContextId;
        }

        public void execute() {
            updateBusinessContext(this);
        }
    }

    public UpdateBusinessContextArguments updateBusinessContext() {
        return new UpdateBusinessContextArguments();
    }

    private void updateBusinessContext(UpdateBusinessContextArguments arguments) {
        if (!StringUtils.isEmpty(arguments.getName())) {
            dslContext.update(BIZ_CTX)
                    .set(BIZ_CTX.NAME, arguments.getName())
                    .set(BIZ_CTX.LAST_UPDATED_BY, arguments.getUserId())
                    .set(BIZ_CTX.LAST_UPDATE_TIMESTAMP, arguments.getTimestamp())
                    .where(BIZ_CTX.BIZ_CTX_ID.eq(arguments.getBusinessContextId()))
                    .execute();
        }
    }
}
