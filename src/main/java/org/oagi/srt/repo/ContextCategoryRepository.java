package org.oagi.srt.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.count;
import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.gateway.http.helper.filter.ContainsFilterBuilder.contains;

@Repository
public class ContextCategoryRepository {
    
    private DSLContext dslContext;
    
    public ContextCategoryRepository(@Autowired DSLContext dslContext) {
        this.dslContext = dslContext;
    }
    
    public class SelectContextCategoryArguments {

        private List<ULong> contextCategoryIds = new ArrayList();
        private List<Condition> conditions = new ArrayList();
        private SortField sortField;
        private int offset = -1;
        private int numberOfRows = -1;

        public SelectContextCategoryArguments setContextCategoryIds(List<ULong> contextCategoryIds) {
            this.contextCategoryIds.addAll(contextCategoryIds);
            return this;
        }

        public SelectContextCategoryArguments setName(String name) {
            if (!StringUtils.isEmpty(name)) {
                conditions.addAll(contains(name, CTX_CATEGORY.NAME));
            }
            return this;
        }

        public SelectContextCategoryArguments setDescription(String description) {
            if (!StringUtils.isEmpty(description)) {
                conditions.addAll(contains(description, CTX_CATEGORY.DESCRIPTION));
            }
            return this;
        }

        public SelectContextCategoryArguments setUpdaterIds(List<ULong> updaterIds) {
            if (!updaterIds.isEmpty()) {
                conditions.add(APP_USER.LOGIN_ID.in(updaterIds));
            }
            return this;
        }

        public SelectContextCategoryArguments setUpdateDate(Date from, Date to) {
            return setUpdateDate(
                    (from != null) ? new Timestamp(from.getTime()).toLocalDateTime() : null,
                    (to != null) ? new Timestamp(to.getTime()).toLocalDateTime() : null
            );
        }

        public SelectContextCategoryArguments setUpdateDate(LocalDateTime from, LocalDateTime to) {
            if (from != null) {
                conditions.add(CTX_CATEGORY.LAST_UPDATE_TIMESTAMP.greaterOrEqual(from));
            }
            if (to != null) {
                conditions.add(CTX_CATEGORY.LAST_UPDATE_TIMESTAMP.lessThan(to));
            }
            return this;
        }

        public SelectContextCategoryArguments setSort(String field, String direction) {
            if (!StringUtils.isEmpty(field)) {
                switch (field) {
                    case "name":
                        if ("asc".equals(direction)) {
                            sortField = CTX_CATEGORY.NAME.asc();
                        } else if ("desc".equals(direction)) {
                            sortField = CTX_CATEGORY.NAME.desc();
                        }

                        break;
                        
                    case "description":
                        if ("asc".equals(direction)) {
                            sortField = CTX_CATEGORY.DESCRIPTION.asc();
                        } else if ("desc".equals(direction)) {
                            sortField = CTX_CATEGORY.DESCRIPTION.desc();
                        }

                    case "lastUpdateTimestamp":
                        if ("asc".equals(direction)) {
                            sortField = CTX_CATEGORY.LAST_UPDATE_TIMESTAMP.asc();
                        } else if ("desc".equals(direction)) {
                            sortField = CTX_CATEGORY.LAST_UPDATE_TIMESTAMP.desc();
                        }

                        break;
                }
            }

            return this;
        }

        public SelectContextCategoryArguments setOffset(int offset, int numberOfRows) {
            this.offset = offset;
            this.numberOfRows = numberOfRows;
            return this;
        }

        public List<Condition> getConditions() {
            if (!contextCategoryIds.isEmpty()) {
                conditions.add(CTX_CATEGORY.CTX_CATEGORY_ID.in(contextCategoryIds));
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
            return selectContextCategories(this, type);
        }
    }
    
    public SelectContextCategoryArguments selectContextCategories() {
        return new SelectContextCategoryArguments(); 
    }
    
    private <E> PaginationResponse<E> selectContextCategories(SelectContextCategoryArguments arguments, Class<? extends E> type) {
        SelectOnConditionStep
                <Record6<ULong, String, String, String, String, LocalDateTime>> step =
                getSelectOnConditionStepForContextCategory();

        SelectConnectByStep
                <Record6<ULong, String, String, String, String, LocalDateTime>> conditionStep =
                step.where(arguments.getConditions());

        int pageCount = dslContext.fetchCount(conditionStep);

        SortField sortField = arguments.getSortField();
        SelectWithTiesAfterOffsetStep
                <Record6<ULong, String, String, String, String, LocalDateTime>> offsetStep = null;
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

    private SelectOnConditionStep
            <Record6<ULong, String, String, String, String, LocalDateTime>> 
    getSelectOnConditionStepForContextCategory() {
        return dslContext.select(
                CTX_CATEGORY.CTX_CATEGORY_ID,
                CTX_CATEGORY.GUID,
                CTX_CATEGORY.NAME,
                CTX_CATEGORY.DESCRIPTION,
                APP_USER.LOGIN_ID.as("last_update_user"),
                CTX_CATEGORY.LAST_UPDATE_TIMESTAMP)
                .from(CTX_CATEGORY)
                .join(APP_USER).on(CTX_CATEGORY.LAST_UPDATED_BY.eq(APP_USER.APP_USER_ID));
    }

    public Map<Long, Boolean> used(List<Long> contextCategoryIds) {
        if (contextCategoryIds == null || contextCategoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return dslContext.select(CTX_SCHEME.CTX_CATEGORY_ID,
                coalesce(count(CTX_SCHEME.CTX_SCHEME_ID), 0))
                .from(CTX_SCHEME)
                .where(CTX_SCHEME.CTX_CATEGORY_ID.in(
                        contextCategoryIds.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList())))
                .groupBy(CTX_SCHEME.CTX_CATEGORY_ID)
                .fetch().stream().collect(Collectors.toMap(e -> e.value1().longValue(), e -> e.value2() > 0));
    }
}
