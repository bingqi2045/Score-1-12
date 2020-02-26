package org.oagi.srt.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContext;
import org.oagi.srt.gateway.http.api.context_management.data.BusinessContextValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BusinessContextRepository {

    private DSLContext dslContext;

    public BusinessContextRepository(@Autowired DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    
    public class SelectBusinessContextArguments {

        private ULong topLevelAbieId;
        private List<ULong> bizCtxIds = new ArrayList();
        private List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberofRows = -1;

        public SelectBusinessContextArguments setTopLevelAbieId(Long topLevelAbieId) {
            if (topLevelAbieId != null) {
                return setTopLevelAbieId(ULong.valueOf(topLevelAbieId));
            }
            return this;
        }

        public SelectBusinessContextArguments setTopLevelAbieId(ULong topLevelAbieId) {
            this.topLevelAbieId = topLevelAbieId;
            return this;
        }

        public SelectBusinessContextArguments setName(String name) {
            if (!StringUtils.isEmpty(name)) {
                conditions.add(BIZ_CTX.NAME.containsIgnoreCase(name.trim()));
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

        public SelectBusinessContextArguments setOffset(int offset, int numberofRows) {
            this.offset = offset;
            this.numberofRows = numberofRows;
            return this;
        }

        public List<Condition> getConditions() {
            if (topLevelAbieId != null) {
                List<ULong> bizCtxIds = dslContext.selectDistinct(
                        BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                        .from(BIZ_CTX_ASSIGNMENT)
                        .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(topLevelAbieId))
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

        public int getNumberofRows() {
            return numberofRows;
        }

        public <E> PaginationResponse<E> fetchInto(Class<? extends E> type) {
            return selectBusinessContexts(this, type);
        }
    }

    public SelectBusinessContextArguments selectBusinessContexts() {
        return new SelectBusinessContextArguments();
    }

    private SelectOnConditionStep
            <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>>
    getSelectOnConditionStepForBusinessContext() {
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
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> step = getSelectOnConditionStepForBusinessContext();

        SelectConnectByStep
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> conditionStep = step.where(arguments.getConditions());

        int pageCount = dslContext.fetchCount(conditionStep);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep
                <Record6<ULong, String, String, LocalDateTime, String, LocalDateTime>> offsetStep = null;
        if (sortField != null) {
            if (arguments.getOffset() >= 0 && arguments.getNumberofRows() >= 0) {
                offsetStep = conditionStep.orderBy(sortField)
                        .limit(arguments.getOffset(), arguments.getNumberofRows());
            }
        } else {
            if (arguments.getOffset() >= 0 && arguments.getNumberofRows() >= 0) {
                offsetStep = conditionStep
                        .limit(arguments.getOffset(), arguments.getNumberofRows());
            }
        }

        return new PaginationResponse<>(pageCount,
                (offsetStep != null) ?
                        offsetStep.fetchInto(type) : conditionStep.fetchInto(type));        
    }

    public BusinessContext findBusinessContextByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
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

    public boolean used(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return false;
        }
        return dslContext.selectCount()
                .from(BIZ_CTX_ASSIGNMENT)
                .where(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public List<BusinessContext> findBusinessContextsByBizCtxIdIn(List<Long> bizCtxIds) {
        if (bizCtxIds == null || bizCtxIds.isEmpty()) {
            return Collections.emptyList();
        }

        return bizCtxIds.stream()
                .map(e -> findBusinessContextByBizCtxId(e))
                .collect(Collectors.toList());
    }

    private SelectOnConditionStep
            <Record8<ULong, ULong, ULong, String, ULong, String, ULong, String>>
    getSelectOnConditionStepForBusinessContextValue() {
        return dslContext.select(
                BIZ_CTX_VALUE.BIZ_CTX_VALUE_ID,
                BIZ_CTX_VALUE.BIZ_CTX_ID,
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.NAME.as("ctx_category_name"),
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME.as("ctx_scheme_name"),
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.VALUE.as("ctx_scheme_value")
        ).from(BIZ_CTX_VALUE)
                .join(CTX_SCHEME_VALUE).on(BIZ_CTX_VALUE.CTX_SCHEME_VALUE_ID.equal(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID))
                .join(CTX_SCHEME).on(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.equal(CTX_SCHEME.CTX_SCHEME_ID))
                .join(CTX_CATEGORY).on(CTX_SCHEME.CTX_CATEGORY_ID.equal(CTX_CATEGORY.CTX_CATEGORY_ID));
    }

    public List<Long> findBizCtxIdsByTopLevelAbieId(Long topLevelAbieId) {
        if (topLevelAbieId == null || topLevelAbieId <= 0L) {
            return Collections.emptyList();
        }

        return dslContext.select(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                .from(BIZ_CTX_ASSIGNMENT)
                .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchInto(Long.class);
    }

    public List<BusinessContextValue> findBusinessContextValues() {
        return getSelectOnConditionStepForBusinessContextValue()
                .fetchInto(BusinessContextValue.class);
    }

    public List<BusinessContextValue> findBusinessContextValuesByBizCtxId(long bizCtxId) {
        if (bizCtxId <= 0L) {
            return Collections.emptyList();
        }
        return getSelectOnConditionStepForBusinessContextValue()
                .where(BIZ_CTX_VALUE.BIZ_CTX_ID.eq(ULong.valueOf(bizCtxId)))
                .fetchInto(BusinessContextValue.class);
    }
}
