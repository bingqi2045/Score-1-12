package org.oagi.score.repo.api.impl.jooq.businesscontext;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.base.ScoreUser;
import org.oagi.score.repo.api.businesscontext.ContextSchemeWriteRepository;
import org.oagi.score.repo.api.businesscontext.model.ContextSchemeValue;
import org.oagi.score.repo.api.businesscontext.model.CreateContextSchemeRequest;
import org.oagi.score.repo.api.businesscontext.model.CreateContextSchemeResponse;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CtxSchemeRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CtxSchemeValueRecord;
import org.oagi.score.repo.api.security.AccessControl;

import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.oagi.score.repo.api.base.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.base.ScoreRole.END_USER;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_SCHEME;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_SCHEME_VALUE;
import static org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils.randomGuid;

public class JooqContextSchemeWriteRepository
        extends JooqScoreRepository
        implements ContextSchemeWriteRepository {

    public JooqContextSchemeWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateContextSchemeResponse createContextScheme(
            CreateContextSchemeRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        CtxSchemeRecord record = new CtxSchemeRecord();

        record.setGuid(randomGuid());
        record.setSchemeId(request.getSchemeId());
        record.setSchemeName(request.getSchemeName());
        record.setDescription(request.getDescription());
        record.setSchemeAgencyId(request.getSchemeAgencyId());
        record.setSchemeVersionId(request.getSchemeVersionId());
        record.setCtxCategoryId(ULong.valueOf(request.getContextCategoryId()));
        record.setCreatedBy(requesterUserId);
        record.setLastUpdatedBy(requesterUserId);
        record.setCreationTimestamp(timestamp);
        record.setLastUpdateTimestamp(timestamp);

        BigInteger contextSchemeId = dslContext().insertInto(CTX_SCHEME)
                .set(record)
                .returning(CTX_SCHEME.CTX_SCHEME_ID)
                .fetchOne().getCtxSchemeId().toBigInteger();

        CreateContextSchemeResponse response = new CreateContextSchemeResponse(contextSchemeId);

        for (ContextSchemeValue contextSchemeValue : request.getContextSchemeValues()) {
            CtxSchemeValueRecord valueRecord = new CtxSchemeValueRecord();

            valueRecord.setGuid(randomGuid());
            valueRecord.setValue(contextSchemeValue.getValue());
            valueRecord.setMeaning(contextSchemeValue.getMeaning());
            valueRecord.setOwnerCtxSchemeId(ULong.valueOf(contextSchemeId));

            BigInteger contextSchemeValueId = dslContext().insertInto(CTX_SCHEME_VALUE)
                    .set(valueRecord)
                    .returning(CTX_SCHEME_VALUE.CTX_SCHEME_VALUE_ID)
                    .fetchOne().getCtxSchemeValueId().toBigInteger();

            response.addContextSchemeValue(new ContextSchemeValue(
                    contextSchemeValueId,
                    valueRecord.getGuid(),
                    contextSchemeValue.getValue()
            ));
        }

        return response;
    }

}
