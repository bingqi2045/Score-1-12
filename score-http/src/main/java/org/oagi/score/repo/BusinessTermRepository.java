package org.oagi.score.repo;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.business_term_management.data.AssignedBusinessTermListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.*;
import static org.oagi.score.gateway.http.helper.filter.ContainsFilterBuilder.contains;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class BusinessTermRepository {

    @Autowired
    private DSLContext dslContext;

    private SelectConditionStep<Record12<
            String,ULong,String,String,String,String,
            String,ULong,String,String,String,LocalDateTime>> getAsbieBiztermAssignmentList(
                    AssignedBusinessTermListRequest request) {
        List<Condition> conditions = setConditions(request);
        if (request.getBieId() != null) {
            conditions.add(ASBIE.ASBIE_ID.eq(ULong.valueOf(request.getBieId())));
        }
        if (StringUtils.hasLength(request.getBieDen())) {
            conditions.add(ASCC.DEN.contains(request.getBieDen()));
        }
        if (StringUtils.hasLength(request.getIsPrimary())) {
            conditions.add(ASBIE_BIZTERM.PRIMARY_INDICATOR.contains(request.getIsPrimary().equals("true") ? "1" : "0"));
        }
        if (StringUtils.hasLength(request.getTypeCode())) {
            conditions.add(ASBIE_BIZTERM.TYPE_CODE.contains(request.getTypeCode()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }
        conditions.stream().forEach(System.out::println);

        return dslContext.select(
                        inline("ASBIE").as("bieType"),
                        ASBIE_BIZTERM.ASBIE_BIZTERM_ID.as("assignedBtId"),
                        ASBIE_BIZTERM.PRIMARY_INDICATOR.as("isPrimary"),
                        ASBIE_BIZTERM.TYPE_CODE.as("typeCode"),
                        ASCC.DEN.as("bieDen"),
                        BUSINESS_TERM.BUSINESS_TERM_,
                        BUSINESS_TERM.EXTERNAL_REF_URI.as("externalReferenceUri"),
                        RELEASE.RELEASE_ID,
                        RELEASE.RELEASE_NUM,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdatedBy"),
                        APP_USER.LOGIN_ID,
                        ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.as("lastUpdateTimestamp"))
                .from(ASBIE_BIZTERM)
                .join(ASCC_BIZTERM).on(ASBIE_BIZTERM.ASCC_BIZTERM_ID.eq(ASCC_BIZTERM.ASCC_BIZTERM_ID))
                .join(ASCC).on(ASCC_BIZTERM.ASCC_ID.eq(ASCC.ASCC_ID))
                .join(BUSINESS_TERM).on(and(
                        ASCC_BIZTERM.BUSINESS_TERM_ID.eq(BUSINESS_TERM.BUSINESS_TERM_ID)
                ))
//               next 3 joins to get release information
                .join(ASBIE).on(ASBIE_BIZTERM.ASBIE_ID.eq(ASBIE.ASBIE_ID))
                .join(ASCC_MANIFEST).on(ASBIE.BASED_ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                .join(RELEASE).on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
//              next joins to get user information
                .join(APP_USER.as("appUserUpdater"))
                .on(ASBIE_BIZTERM.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(ASBIE_BIZTERM.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    private SelectConditionStep<Record12<
            String,ULong,String,String,String,String,
            String,ULong,String,String,String,LocalDateTime>>
    getBbieBiztermAssignmentList(AssignedBusinessTermListRequest request) {
        List<Condition> conditions = setConditions(request);;
        if (request.getBieId() != null) {
            conditions.add(BBIE.BBIE_ID.eq(ULong.valueOf(request.getBieId())));
        }
        if (StringUtils.hasLength(request.getBieDen())) {
            if (StringUtils.hasLength(request.getBieDen())) {
                conditions.add(BCC.DEN.contains(request.getBieDen()));
            }
        }
        if (StringUtils.hasLength(request.getIsPrimary())) {
            conditions.add(BBIE_BIZTERM.PRIMARY_INDICATOR.contains(request.getIsPrimary().equals("true") ? "1" : "0"));
        }
        if (StringUtils.hasLength(request.getTypeCode())) {
            conditions.add(BBIE_BIZTERM.TYPE_CODE.contains(request.getTypeCode()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.greaterOrEqual(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                        inline("BBIE").as("bieType"),
                        BBIE_BIZTERM.BBIE_BIZTERM_ID.as("assignedBtId"),
                        BBIE_BIZTERM.PRIMARY_INDICATOR.as("isPrimary"),
                        BBIE_BIZTERM.TYPE_CODE.as("typeCode"),
                        BCC.DEN.as("den"),
                        BUSINESS_TERM.BUSINESS_TERM_,
                        BUSINESS_TERM.EXTERNAL_REF_URI.as("externalReferenceUri"),
                        RELEASE.RELEASE_ID,
                        RELEASE.RELEASE_NUM,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdatedBy"),
                        APP_USER.LOGIN_ID,
                        BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.as("lastUpdateTimestamp"))
                .from(BBIE_BIZTERM)
                .join(BCC_BIZTERM).on(BBIE_BIZTERM.BCC_BIZTERM_ID.eq(BCC_BIZTERM.BCC_BIZTERM_ID))
                .join(BCC).on(BCC_BIZTERM.BCC_ID.eq(BCC.BCC_ID))
                .join(BUSINESS_TERM).on(and(
                        BCC_BIZTERM.BUSINESS_TERM_ID.eq(BUSINESS_TERM.BUSINESS_TERM_ID)
                ))
//               next 3 joins to get release information
                .join(BBIE).on(BBIE_BIZTERM.BBIE_ID.eq(BBIE.BBIE_ID))
                .join(BCC_MANIFEST).on(BBIE.BASED_BCC_MANIFEST_ID.eq(BCC_MANIFEST.BCC_MANIFEST_ID))
                .join(RELEASE).on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
//              next joins to get user information
                .join(APP_USER.as("appUserUpdater"))
                .on(BBIE_BIZTERM.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(BBIE_BIZTERM.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    private List<Condition> setConditions(AssignedBusinessTermListRequest request) {
        List<Condition> conditions = new ArrayList<>();
        if (StringUtils.hasLength(request.getBusinessTerm())) {
            conditions.addAll(contains(request.getBusinessTerm(), BUSINESS_TERM.BUSINESS_TERM_));
        }
        if (StringUtils.hasLength(request.getBusinessContext())) {
            conditions.addAll(contains(request.getBusinessContext(), BIZ_CTX.NAME));
        }
        if (StringUtils.hasLength(request.getExternalReferenceUri())) {
            conditions.addAll(contains(request.getExternalReferenceUri(), BUSINESS_TERM.EXTERNAL_REF_URI));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(APP_USER.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(APP_USER.as("appUserUpdater").LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        return conditions;
    }

    public <E> PaginationResponse<E> getBieBiztermList(AssignedBusinessTermListRequest request,
                                                        Class<? extends E> type) {

        SelectOrderByStep select = null;
        if (request.getBieTypes().contains("ASBIE")) {
            select = getAsbieBiztermAssignmentList(request);
        }
        if (request.getBieTypes().contains("BBIE")) {
            select = (select != null) ? select.union(getBbieBiztermAssignmentList(request)) :
                    getBbieBiztermAssignmentList(request);
        }

        int pageCount = dslContext.fetchCount(select);
        Optional<SortField> sortField = setSort(request.getPageRequest().getSortActive(),
                request.getPageRequest().getSortDirection());
        SelectWithTiesAfterOffsetStep offsetStep = null;
        if (sortField != null) {
            if (request.getPageRequest().getOffset() >= 0 && request.getPageRequest().getPageSize() >= 0) {
                offsetStep = select.orderBy(sortField.get()).limit(request.getPageRequest().getOffset(),
                        request.getPageRequest().getPageSize());
            }
        } else {
            if (request.getPageRequest().getOffset() >= 0 && request.getPageRequest().getPageSize() >= 0) {
                offsetStep = select.limit(request.getPageRequest().getOffset(), request.getPageRequest().getPageSize());
            }
        }

        return new PaginationResponse<>(pageCount,
                (offsetStep != null) ? offsetStep.fetchInto(type) : select.fetchInto(type));
    }

    public Optional<SortField> setSort(String field, String direction) {
        Optional<SortField> sortField = Optional.empty();
        if (StringUtils.hasLength(field)) {
            switch (field) {
                case "bieType":
                    if ("asc".equals(direction)) {
                        sortField = Optional.of(field("type").asc());
                    } else if ("desc".equals(direction)) {
                        sortField = Optional.of(field("type").desc());
                    }

                    break;

                case "den":
                    if ("asc".equals(direction)) {
                        sortField = Optional.of(field("den").asc());
                    } else if ("desc".equals(direction)) {
                        sortField = Optional.of(field("den").desc());
                    }

                    break;

                case "releaseNum":
                    if ("asc".equals(direction)) {
                        sortField = Optional.of(RELEASE.RELEASE_NUM.asc());
                    } else if ("desc".equals(direction)) {
                        sortField = Optional.of(RELEASE.RELEASE_NUM.desc());
                    }

                    break;

                case "lastUpdateTimestamp":
                    if ("asc".equals(direction)) {
                        sortField = Optional.of(field("lastUpdateTimestamp").asc());
                    } else if ("desc".equals(direction)) {
                        sortField = Optional.of(field("lastUpdateTimestamp").desc());
                    }

                    break;
            }
        }
        return sortField;
    }

}
