package org.oagi.score.repo.api.impl.jooq.businessterm;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businessterm.BusinessTermAssignmentReadRepository;
import org.oagi.score.repo.api.businessterm.model.AssignedBusinessTerm;
import org.oagi.score.repo.api.businessterm.model.GetAssignedBusinessTermListRequest;
import org.oagi.score.repo.api.businessterm.model.GetAssignedBusinessTermListResponse;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.security.AccessControl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.inline;
import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.contains;
import static org.oagi.score.repo.api.impl.utils.StringUtils.trim;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqBusinessTermAssignmentReadRepository
        extends JooqScoreRepository
        implements BusinessTermAssignmentReadRepository {

    public JooqBusinessTermAssignmentReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    private RecordMapper<Record, AssignedBusinessTerm> mapper() {
        return record -> {
            AssignedBusinessTerm assignedBusinessTerm = new AssignedBusinessTerm();
            assignedBusinessTerm.setAssignedBtId(record.get("assignedBtId", BigInteger.class));
            assignedBusinessTerm.setBieType(record.get("bieType", String.class));
            assignedBusinessTerm.setIsPrimary(record.get("isPrimary", String.class));
            assignedBusinessTerm.setTypeCode(record.get("typeCode", String.class));
            assignedBusinessTerm.setDen(record.get("den", String.class));
            assignedBusinessTerm.setBusinessTerm(record.get(BUSINESS_TERM.BUSINESS_TERM_));
            assignedBusinessTerm.setExternalReferenceUri(record.get(BUSINESS_TERM.EXTERNAL_REF_URI));
            assignedBusinessTerm.setOwner(record.get(APP_USER.LOGIN_ID));
            assignedBusinessTerm.setLastUpdateUser(record.get(APP_USER.as("appUserUpdater").LOGIN_ID));
            assignedBusinessTerm.setLastUpdateTimestamp(
                    Date.from(record.get("lastUpdateTimestamp", LocalDateTime.class)
                            .atZone(ZoneId.systemDefault()).toInstant()));
            return assignedBusinessTerm;
        };
    }

    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetAssignedBusinessTermListResponse getBusinessTermAssignments1(
            GetAssignedBusinessTermListRequest request) throws ScoreDataAccessException {

//        BusinessTerm businessTerm = null;
//
//        BigInteger businessTermId = request.getBusinessTermId();
//        if (!isNull(businessTermId)) {
//            businessTerm = (BusinessTerm) select()
//                    .where(BUSINESS_TERM.BUSINESS_TERM_ID.eq(ULong.valueOf(businessTermId)))
//                    .fetchOne(mapper());
//        }
//
//        return new GetBusinessTermResponse(businessTerm);
        return null;
    }

    public SelectOrderByStep getAsbieBiztermList(List<Condition> conditions) {

        return dslContext().select(
                        ASBIE_BIZTERM.ASBIE_BIZTERM_ID.as("assignedBtId"),
                        inline("ASBIE").as("bieType"),
                        ASBIE_BIZTERM.PRIMARY_INDICATOR.as("isPrimary"),
                        ASBIE_BIZTERM.TYPE_CODE.as("typeCode"),
                        ASCC.DEN.as("den"),
                        BUSINESS_TERM.BUSINESS_TERM_,
                        BUSINESS_TERM.EXTERNAL_REF_URI,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdateUser"),
                        APP_USER.LOGIN_ID,
                        ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.as("lastUpdateTimestamp"))
                .from(ASBIE_BIZTERM)
                .join(ASCC_BIZTERM).on(ASBIE_BIZTERM.ASCC_BIZTERM_ID.eq(ASCC_BIZTERM.ASCC_BIZTERM_ID))
                .join(ASCC).on(ASCC_BIZTERM.ASCC_ID.eq(ASCC.ASCC_ID))
                .join(BUSINESS_TERM).on(and(
                        ASCC_BIZTERM.BUSINESS_TERM_ID.eq(BUSINESS_TERM.BUSINESS_TERM_ID)
                ))
                .join(APP_USER.as("appUserUpdater"))
                .on(ASBIE_BIZTERM.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(ASBIE_BIZTERM.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    public SelectOrderByStep getBbieBiztermList(List<Condition> conditions) {

        return dslContext().select(
                        BBIE_BIZTERM.BBIE_BIZTERM_ID.as("assignedBtId"),
                        inline("BBIE").as("bieType"),
                        BBIE_BIZTERM.PRIMARY_INDICATOR.as("isPrimary"),
                        BBIE_BIZTERM.TYPE_CODE.as("typeCode"),
                        BCC.DEN.as("den"),
                        BUSINESS_TERM.BUSINESS_TERM_,
                        BUSINESS_TERM.EXTERNAL_REF_URI,
                        APP_USER.as("appUserUpdater").LOGIN_ID.as("lastUpdateUser"),
                        APP_USER.LOGIN_ID,
                        BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP.as("lastUpdateTimestamp"))
                .from(BBIE_BIZTERM)
                .join(BCC_BIZTERM).on(BBIE_BIZTERM.BCC_BIZTERM_ID.eq(BCC_BIZTERM.BCC_BIZTERM_ID))
                .join(BCC).on(BCC_BIZTERM.BCC_ID.eq(BCC.BCC_ID))
                .join(BUSINESS_TERM).on(and(
                        BCC_BIZTERM.BUSINESS_TERM_ID.eq(BUSINESS_TERM.BUSINESS_TERM_ID)
                ))
                .join(APP_USER.as("appUserUpdater"))
                .on(BBIE_BIZTERM.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .join(APP_USER)
                .on(BBIE_BIZTERM.CREATED_BY.eq(APP_USER.APP_USER_ID))
                .where(conditions);
    }

    private List<Condition> getConditions(GetAssignedBusinessTermListRequest request) {
        List<Condition> conditions = new ArrayList();

        if (request.getAssignedBtIdList() != null && !request.getAssignedBtIdList().isEmpty()) {
            if (request.getAssignedBtIdList().size() == 1) {
                conditions.add(ASBIE_BIZTERM.ASBIE_BIZTERM_ID.eq(
                        ULong.valueOf(request.getAssignedBtIdList().iterator().next()))
                        .or(BBIE_BIZTERM.BBIE_BIZTERM_ID.eq(
                                ULong.valueOf(request.getAssignedBtIdList().iterator().next()))));
            } else {
                conditions.add(ASBIE_BIZTERM.ASBIE_BIZTERM_ID.in(
                        request.getAssignedBtIdList().stream()
                                .map(e -> ULong.valueOf(e)).collect(Collectors.toList()))
                        .or(ASBIE_BIZTERM.ASBIE_BIZTERM_ID.in(
                                request.getAssignedBtIdList().stream()
                                        .map(e -> ULong.valueOf(e)).collect(Collectors.toList()))));
            }
        }
        if (StringUtils.hasLength(request.getBusinessTerm())) {
            conditions.addAll(contains(request.getBusinessTerm(), BUSINESS_TERM.BUSINESS_TERM_));
        }
        if (StringUtils.hasLength(request.getExternalReferenceUri())) {
            conditions.addAll(contains(request.getExternalReferenceUri(), BUSINESS_TERM.EXTERNAL_REF_URI));
        }
        if (StringUtils.hasLength(request.getBieDen())) {
            conditions.add(ASCC.DEN.contains(request.getBieDen()).or(BCC.DEN.contains(request.getBieDen())));
        }
        if (StringUtils.hasLength(request.getTypeCode())) {
            conditions.add(ASBIE_BIZTERM.TYPE_CODE.contains(request.getTypeCode())
                    .or(BBIE_BIZTERM.TYPE_CODE.contains(request.getTypeCode())));
        }
        if (StringUtils.hasLength(request.getIsPrimary())) {
            conditions.add(ASBIE_BIZTERM.PRIMARY_INDICATOR.eq(request.getIsPrimary())
                    .or(BBIE_BIZTERM.PRIMARY_INDICATOR.eq(request.getIsPrimary())));
        }
        if (!request.getUpdaterUsernameList().isEmpty()) {
            conditions.add(APP_USER.as("updater").LOGIN_ID.in(
                    new HashSet<>(request.getUpdaterUsernameList()).stream()
                            .filter(e -> StringUtils.hasLength(e)).map(e -> trim(e)).collect(Collectors.toList())
            ));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BUSINESS_TERM.LAST_UPDATE_TIMESTAMP.greaterOrEqual(request.getUpdateStartDate()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BUSINESS_TERM.LAST_UPDATE_TIMESTAMP.lessThan(request.getUpdateEndDate()));
        }
        return conditions;
    }


    private SortField getSortField(GetAssignedBusinessTermListRequest request) {
        if (!StringUtils.hasLength(request.getSortActive())) {
            return null;
        }

        Field field = DSL.field(trim(request.getSortActive()).toLowerCase());

        return (request.getSortDirection() == ASC) ? field.asc() : field.desc();
    }

    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetAssignedBusinessTermListResponse getBusinessTermAssignments(
            GetAssignedBusinessTermListRequest request) throws ScoreDataAccessException {
        List<Condition> conditions = getConditions(request);


        SelectOrderByStep select = null;
        if (request.getBieTypes().contains("ASBIE")) {
            select = getAsbieBiztermList(conditions);
        }
        if (request.getBieTypes().contains("BBIE")) {
            select = (select != null) ? select.union(getBbieBiztermList(conditions)) :
                    getBbieBiztermList(conditions);
        }

        SortField sortField = getSortField(request);
        int length = dslContext().fetchCount(select);
        SelectFinalStep finalStep;
        if (sortField == null) {
            if (request.isPagination()) {
                finalStep = select.limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = select;
            }
        } else {
            if (request.isPagination()) {
                finalStep = select.orderBy(sortField)
                        .limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = select.orderBy(sortField);
            }
        }
        return new GetAssignedBusinessTermListResponse(
                finalStep.fetch(mapper()),
                request.getPageIndex(),
                request.getPageSize(),
                length
        );
    }
}
