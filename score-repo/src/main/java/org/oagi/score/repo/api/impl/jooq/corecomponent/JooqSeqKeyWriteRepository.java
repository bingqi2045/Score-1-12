package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.DSLContext;
import org.jooq.UpdateSetStep;
import org.jooq.UpdateWhereStep;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.SeqKeyRecord;
import org.oagi.score.repo.api.security.AccessControl;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqSeqKeyWriteRepository
        extends JooqScoreRepository
        implements SeqKeyWriteRepository {

    public JooqSeqKeyWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public CreateSeqKeyResponse createSeqKey(CreateSeqKeyRequest request) throws ScoreDataAccessException {

        SeqKeyRecord record = new SeqKeyRecord();
        record.setFromAccId(ULong.valueOf(request.getFromAccId()));
        record.setType(org.oagi.score.repo.api.impl.jooq.entity.enums.SeqKeyType.valueOf(
                request.getType().name().toLowerCase()));
        record.setCcId(ULong.valueOf(request.getCcId()));
        record.setSeqKeyId(
                dslContext().insertInto(SEQ_KEY)
                        .set(record)
                        .returning(SEQ_KEY.SEQ_KEY_ID)
                        .fetchOne().getSeqKeyId()
        );

        switch (record.getType()) {
            case ascc:
                dslContext().update(ASCC)
                        .set(ASCC.SEQ_KEY_ID, record.getSeqKeyId())
                        .where(ASCC.ASCC_ID.eq(record.getCcId()))
                        .execute();
                break;

            case bcc:
                dslContext().update(BCC)
                        .set(BCC.SEQ_KEY_ID, record.getSeqKeyId())
                        .where(BCC.BCC_ID.eq(record.getCcId()))
                        .execute();
                break;
        }

        SeqKey seqKey = new SeqKey();
        seqKey.setSeqKeyId(record.getSeqKeyId().toBigInteger());
        seqKey.setFromAccId(record.getFromAccId().toBigInteger());
        seqKey.setSeqKeyType(org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKeyType.valueOf(
                record.getType().name().toUpperCase()));
        seqKey.setCcId(record.getCcId().toBigInteger());

        return new CreateSeqKeyResponse(seqKey);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateSeqKeyResponse updateSeqKey(UpdateSeqKeyRequest request) throws ScoreDataAccessException {
        SeqKey seqKey = request.getSeqKey();

        SeqKeyRecord record = dslContext().selectFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(ULong.valueOf(seqKey.getSeqKeyId())))
                .fetchOne();

        UpdateSetStep step = dslContext().update(SEQ_KEY);
        SeqKey prev = seqKey.getPrevSeqKey();
        if (prev == null) {
            step = step.setNull(SEQ_KEY.PREV_SEQ_KEY_ID);
        } else {
            step = step.set(SEQ_KEY.PREV_SEQ_KEY_ID, ULong.valueOf(prev.getSeqKeyId()));
        }

        SeqKey next = seqKey.getNextSeqKey();
        if (next == null) {
            step = step.setNull(SEQ_KEY.NEXT_SEQ_KEY_ID);
        } else {
            step = step.set(SEQ_KEY.NEXT_SEQ_KEY_ID, ULong.valueOf(next.getSeqKeyId()));
        }

        int affectedRows = 0;
        if (step instanceof UpdateWhereStep) {
            affectedRows = ((UpdateWhereStep) step)
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(ULong.valueOf(seqKey.getSeqKeyId())))
                    .execute();
        }

        return new UpdateSeqKeyResponse((affectedRows == 0) ? null : seqKey.getSeqKeyId());
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteSeqKeyResponse deleteSeqKey(DeleteSeqKeyRequest request) throws ScoreDataAccessException {
        SeqKey seqKey = request.getSeqKey();

        int affectedRows = dslContext().deleteFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(ULong.valueOf(seqKey.getSeqKeyId())))
                .execute();

        return new DeleteSeqKeyResponse((affectedRows == 0) ? null : seqKey.getSeqKeyId());
    }

}
