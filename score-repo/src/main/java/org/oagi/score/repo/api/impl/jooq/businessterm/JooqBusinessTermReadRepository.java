package org.oagi.score.repo.api.impl.jooq.businessterm;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businessterm.BusinessTermReadRepository;
import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.base.SortDirection.ASC;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.APP_USER;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BUSINESS_TERM;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.contains;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.isNull;
import static org.oagi.score.repo.api.impl.utils.StringUtils.trim;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqBusinessTermReadRepository
        extends JooqScoreRepository
        implements BusinessTermReadRepository {

    public JooqBusinessTermReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    private SelectOnConditionStep select() {
        return dslContext().select(
                BUSINESS_TERM.BUSINESS_TERM_ID,
                BUSINESS_TERM.GUID,
                BUSINESS_TERM.BUSINESS_TERM_,
                BUSINESS_TERM.DEFINITION,
                BUSINESS_TERM.EXTERNAL_REF_ID,
                BUSINESS_TERM.EXTERNAL_REF_URI,
                APP_USER.as("creator").APP_USER_ID.as("creator_user_id"),
                APP_USER.as("creator").LOGIN_ID.as("creator_login_id"),
                APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer"),
                APP_USER.as("updater").APP_USER_ID.as("updater_user_id"),
                APP_USER.as("updater").LOGIN_ID.as("updater_login_id"),
                APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer"),
                BUSINESS_TERM.CREATION_TIMESTAMP,
                BUSINESS_TERM.LAST_UPDATE_TIMESTAMP)
                .from(BUSINESS_TERM)
                .join(APP_USER.as("creator")).on(BUSINESS_TERM.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater")).on(BUSINESS_TERM.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID));
    }

    private RecordMapper<Record, BusinessTerm> mapper() {
        return record -> {
            BusinessTerm businessTerm = new BusinessTerm();
            businessTerm.setBusinessTermId(record.get(BUSINESS_TERM.BUSINESS_TERM_ID).toBigInteger());
            businessTerm.setGuid(record.get(BUSINESS_TERM.GUID));
            businessTerm.setBusinessTerm(record.get(BUSINESS_TERM.BUSINESS_TERM_));
            businessTerm.setDefinition(record.get(BUSINESS_TERM.DEFINITION));
            businessTerm.setExternalReferenceId(record.get(BUSINESS_TERM.EXTERNAL_REF_ID));
            businessTerm.setExternalReferenceUri(record.get(BUSINESS_TERM.EXTERNAL_REF_URI));
//            todo add if it's needed
//            businessTerm.setUsed(dslTerm().selectCount().from(BUSINESS_TERM_ASSIGNMENT)
//                    .where(BUSINESS_TERM_ASSIGNMENT.BUSINESS_TERM_ID.eq(record.get(BUSINESS_TERM.BUSINESS_TERM_ID)))
//                    .fetchOneInto(Integer.class) > 0);
            businessTerm.setCreatedBy(new ScoreUser(
                    record.get(APP_USER.as("creator").APP_USER_ID.as("creator_user_id")).toBigInteger(),
                    record.get(APP_USER.as("creator").LOGIN_ID.as("creator_login_id")),
                    (byte) 1 == record.get(APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer")) ? DEVELOPER : END_USER
            ));
            businessTerm.setLastUpdatedBy(new ScoreUser(
                    record.get(APP_USER.as("updater").APP_USER_ID.as("updater_user_id")).toBigInteger(),
                    record.get(APP_USER.as("updater").LOGIN_ID.as("updater_login_id")),
                    (byte) 1 == record.get(APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer")) ? DEVELOPER : END_USER
            ));
            businessTerm.setCreationTimestamp(
                    Date.from(record.get(BUSINESS_TERM.CREATION_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            businessTerm.setLastUpdateTimestamp(
                    Date.from(record.get(BUSINESS_TERM.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
            return businessTerm;
        };
    }

    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetBusinessTermResponse getBusinessTerm(
            GetBusinessTermRequest request) throws ScoreDataAccessException {
        BusinessTerm businessTerm = null;

        BigInteger businessTermId = request.getBusinessTermId();
        if (!isNull(businessTermId)) {
            businessTerm = (BusinessTerm) select()
                    .where(BUSINESS_TERM.BUSINESS_TERM_ID.eq(ULong.valueOf(businessTermId)))
                    .fetchOne(mapper());
        }

        return new GetBusinessTermResponse(businessTerm);
    }


    private Collection<Condition> getConditions(GetBusinessTermListRequest request) {
        List<Condition> conditions = new ArrayList();

        if (request.getBusinessTermIdList() != null && !request.getBusinessTermIdList().isEmpty()) {
            if (request.getBusinessTermIdList().size() == 1) {
                conditions.add(BUSINESS_TERM.BUSINESS_TERM_ID.eq(
                        ULong.valueOf(request.getBusinessTermIdList().iterator().next())
                ));
            } else {
                conditions.add(BUSINESS_TERM.BUSINESS_TERM_ID.in(
                        request.getBusinessTermIdList().stream()
                                .map(e -> ULong.valueOf(e)).collect(Collectors.toList())
                ));
            }
        }
        if (StringUtils.hasLength(request.getBusinessTerm())) {
            conditions.addAll(contains(request.getBusinessTerm(), BUSINESS_TERM.BUSINESS_TERM_));
        }
        if (StringUtils.hasLength(request.getDefinition())) {
            conditions.addAll(contains(request.getDefinition(), BUSINESS_TERM.DEFINITION));
        }
        if (StringUtils.hasLength(request.getExternalRefId())) {
            conditions.addAll(contains(request.getExternalRefId(), BUSINESS_TERM.EXTERNAL_REF_ID));
        }
        if (StringUtils.hasLength(request.getExternalRefUri())) {
            conditions.addAll(contains(request.getExternalRefUri(), BUSINESS_TERM.EXTERNAL_REF_URI));
        }
        if (StringUtils.hasLength(request.getExternalRefUri())) {
            conditions.addAll(contains(request.getExternalRefUri(), BUSINESS_TERM.EXTERNAL_REF_URI));
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

    private SortField getSortField(GetBusinessTermListRequest request) {
        if (!StringUtils.hasLength(request.getSortActive())) {
            return null;
        }

        Field field;
        switch (trim(request.getSortActive()).toLowerCase()) {
            case "name":
                field = BUSINESS_TERM.BUSINESS_TERM_;
                break;

            case "lastupdatetimestamp":
                field = BUSINESS_TERM.LAST_UPDATE_TIMESTAMP;
                break;

            default:
                return null;
        }

        return (request.getSortDirection() == ASC) ? field.asc() : field.desc();
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetBusinessTermListResponse getBusinessTermList(
            GetBusinessTermListRequest request) throws ScoreDataAccessException {
        Collection<Condition> conditions = getConditions(request);
        SelectConditionStep conditionStep = select().where(conditions);

        SortField sortField = getSortField(request);
        int length = dslContext().fetchCount(conditionStep);
        SelectFinalStep finalStep;
        if (sortField == null) {
            if (request.isPagination()) {
                finalStep = conditionStep.limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = conditionStep;
            }
        } else {
            if (request.isPagination()) {
                finalStep = conditionStep.orderBy(sortField)
                        .limit(request.getPageOffset(), request.getPageSize());
            } else {
                finalStep = conditionStep.orderBy(sortField);
            }
        }

        return new GetBusinessTermListResponse(
                finalStep.fetch(mapper()),
                request.getPageIndex(),
                request.getPageSize(),
                length
        );
    }
}
