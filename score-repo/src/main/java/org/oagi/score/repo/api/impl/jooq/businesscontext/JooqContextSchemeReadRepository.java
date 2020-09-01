package org.oagi.score.repo.api.impl.jooq.businesscontext;

import org.jooq.*;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.base.ScoreUser;
import org.oagi.score.repo.api.businesscontext.ContextSchemeReadRepository;
import org.oagi.score.repo.api.businesscontext.model.ContextScheme;
import org.oagi.score.repo.api.businesscontext.model.ContextSchemeValue;
import org.oagi.score.repo.api.businesscontext.model.GetContextSchemeRequest;
import org.oagi.score.repo.api.businesscontext.model.GetContextSchemeResponse;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.security.AccessControl;

import java.math.BigInteger;

import static org.oagi.score.repo.api.base.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.base.ScoreRole.END_USER;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.isNull;

public class JooqContextSchemeReadRepository
        extends JooqScoreRepository
        implements ContextSchemeReadRepository {

    public JooqContextSchemeReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    private SelectOnConditionStep select() {
        return dslContext().select(
                CTX_SCHEME.CTX_SCHEME_ID,
                CTX_SCHEME.GUID,
                CTX_SCHEME.SCHEME_ID,
                CTX_SCHEME.SCHEME_NAME,
                CTX_SCHEME.DESCRIPTION,
                CTX_SCHEME.SCHEME_AGENCY_ID,
                CTX_SCHEME.SCHEME_VERSION_ID,
                CTX_SCHEME.CTX_CATEGORY_ID,
                CTX_SCHEME.CODE_LIST_ID,
                APP_USER.as("creator").APP_USER_ID.as("creator_user_id"),
                APP_USER.as("creator").LOGIN_ID.as("creator_login_id"),
                APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer"),
                APP_USER.as("updater").APP_USER_ID.as("updater_user_id"),
                APP_USER.as("updater").LOGIN_ID.as("updater_login_id"),
                APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer"),
                CTX_SCHEME.CREATION_TIMESTAMP,
                CTX_SCHEME.LAST_UPDATE_TIMESTAMP)
                .from(CTX_SCHEME)
                .join(APP_USER.as("creator")).on(CTX_SCHEME.CREATED_BY.eq(APP_USER.as("creator").APP_USER_ID))
                .join(APP_USER.as("updater")).on(CTX_SCHEME.LAST_UPDATED_BY.eq(APP_USER.as("updater").APP_USER_ID));
    }

    private RecordMapper<Record, ContextScheme> mapper() {
        return record -> {
            ContextScheme contextScheme = new ContextScheme();
            contextScheme.setContextSchemeId(record.get(CTX_SCHEME.CTX_SCHEME_ID).toBigInteger());
            contextScheme.setGuid(record.get(CTX_SCHEME.GUID));
            contextScheme.setSchemeId(record.get(CTX_SCHEME.SCHEME_ID));
            contextScheme.setSchemeName(record.get(CTX_SCHEME.SCHEME_NAME));
            contextScheme.setDescription(record.get(CTX_SCHEME.DESCRIPTION));
            contextScheme.setSchemeAgencyId(record.get(CTX_SCHEME.SCHEME_AGENCY_ID));
            contextScheme.setSchemeVersionId(record.get(CTX_SCHEME.SCHEME_VERSION_ID));
            contextScheme.setContextCategoryId(record.get(CTX_SCHEME.CTX_CATEGORY_ID).toBigInteger());
            contextScheme.setImported(record.get(CTX_SCHEME.CODE_LIST_ID) != null);
            contextScheme.setCreatedBy(new ScoreUser(
                    record.get(APP_USER.as("creator").APP_USER_ID.as("creator_user_id")).toBigInteger(),
                    record.get(APP_USER.as("creator").LOGIN_ID.as("creator_login_id")),
                    (byte) 1 == record.get(APP_USER.as("creator").IS_DEVELOPER.as("creator_is_developer")) ? DEVELOPER : END_USER
            ));
            contextScheme.setLastUpdatedBy(new ScoreUser(
                    record.get(APP_USER.as("updater").APP_USER_ID.as("updater_user_id")).toBigInteger(),
                    record.get(APP_USER.as("updater").LOGIN_ID.as("updater_login_id")),
                    (byte) 1 == record.get(APP_USER.as("updater").IS_DEVELOPER.as("updater_is_developer")) ? DEVELOPER : END_USER
            ));
            contextScheme.setCreationTimestamp(record.get(CTX_CATEGORY.CREATION_TIMESTAMP));
            contextScheme.setLastUpdateTimestamp(record.get(CTX_CATEGORY.LAST_UPDATE_TIMESTAMP));
            return contextScheme;
        };
    }

    private SelectConditionStep selectForValues(BigInteger contextSchemeId) {
        return dslContext().select(
                CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID,
                CTX_SCHEME_VALUE.GUID,
                CTX_SCHEME_VALUE.VALUE,
                CTX_SCHEME_VALUE.MEANING)
                .from(CTX_SCHEME_VALUE)
                .where(CTX_SCHEME_VALUE.OWNER_CTX_SCHEME_ID.eq(ULong.valueOf(contextSchemeId)));
    }

    private RecordMapper<Record, ContextSchemeValue> mapperForValue() {
        return record -> {
            ContextSchemeValue contextSchemeValue = new ContextSchemeValue();
            contextSchemeValue.setContextSchemeValueId(
                    record.get(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID).toBigInteger());
            contextSchemeValue.setGuid(record.get(CTX_SCHEME_VALUE.GUID));
            contextSchemeValue.setValue(record.get(CTX_SCHEME_VALUE.VALUE));
            contextSchemeValue.setMeaning(record.get(CTX_SCHEME_VALUE.MEANING));
            return contextSchemeValue;
        };
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetContextSchemeResponse getContextScheme(
            GetContextSchemeRequest request) throws ScoreDataAccessException {
        ContextScheme contextScheme = null;

        BigInteger contextSchemeId = request.getContextSchemeId();
        if (!isNull(contextSchemeId)) {
            contextScheme = (ContextScheme) select()
                    .where(CTX_SCHEME.CTX_SCHEME_ID.eq(ULong.valueOf(contextSchemeId)))
                    .fetchOne(mapper());

            contextScheme.setContextSchemeValues(
                    selectForValues(contextSchemeId).fetch(mapperForValue())
            );
        }

        return new GetContextSchemeResponse(contextScheme);
    }

}
