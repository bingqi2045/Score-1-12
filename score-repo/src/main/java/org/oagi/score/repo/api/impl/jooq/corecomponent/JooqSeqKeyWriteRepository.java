package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.DSLContext;
import org.jooq.UpdateSetStep;
import org.jooq.UpdateWhereStep;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.SeqKeyRecord;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.security.AccessControl;

import java.util.UUID;

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
        record.setSeqKeyId(UUID.randomUUID().toString());
        record.setFromAccManifestId(request.getFromAccManifestId());
        if (SeqKeyType.ASCC == request.getType()) {
            record.setAsccManifestId(request.getManifestId());
        } else {
            record.setBccManifestId(request.getManifestId());
        }

        dslContext().insertInto(SEQ_KEY)
                .set(record)
                .execute();

        switch (request.getType()) {
            case ASCC:
                dslContext().update(ASCC_MANIFEST)
                        .set(ASCC_MANIFEST.SEQ_KEY_ID, record.getSeqKeyId())
                        .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(record.getAsccManifestId()))
                        .execute();
                break;

            case BCC:
                dslContext().update(BCC_MANIFEST)
                        .set(BCC_MANIFEST.SEQ_KEY_ID, record.getSeqKeyId())
                        .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(record.getBccManifestId()))
                        .execute();
                break;
        }

        SeqKey seqKey = new SeqKey();
        seqKey.setSeqKeyId(record.getSeqKeyId());
        seqKey.setFromAccManifestId(record.getFromAccManifestId());
        if (record.getAsccManifestId() != null) {
            seqKey.setAsccManifestId(record.getAsccManifestId());
        }
        if (record.getBccManifestId() != null) {
            seqKey.setBccManifestId(record.getBccManifestId());
        }

        return new CreateSeqKeyResponse(seqKey);
    }

    private void setPrev(String key, String prev) {
        if (key != null) {
            if (prev != null) {
                dslContext().update(SEQ_KEY)
                        .set(SEQ_KEY.PREV_SEQ_KEY_ID, prev)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(key))
                        .execute();
            } else {
                dslContext().update(SEQ_KEY)
                        .setNull(SEQ_KEY.PREV_SEQ_KEY_ID)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(key))
                        .execute();
            }
        }
    }

    private void setNext(String key, String next) {
        if (key != null) {
            if (next != null) {
                dslContext().update(SEQ_KEY)
                        .set(SEQ_KEY.NEXT_SEQ_KEY_ID, next)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(key))
                        .execute();
            } else {
                dslContext().update(SEQ_KEY)
                        .setNull(SEQ_KEY.NEXT_SEQ_KEY_ID)
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(key))
                        .execute();
            }
        }
    }

    private void brokeLinks(SeqKey seqKey) {
        SeqKeyRecord record = get(seqKey.getSeqKeyId());

        String prev = record.getPrevSeqKeyId();
        String next = record.getNextSeqKeyId();

        setNext(prev, next);
        setPrev(next, prev);

        setPrev(record.getSeqKeyId(), null);
        setNext(record.getSeqKeyId(), null);
    }

    private SeqKeyRecord get(String id) {
        if (!StringUtils.hasLength(id)) {
            return null;
        }
        return dslContext().selectFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(id))
                .fetchOne();
    }

    @Override
    public MoveAfterResponse moveAfter(MoveAfterRequest request) throws ScoreDataAccessException {
        if (request.getAfter() == null) {
            throw new ScoreDataAccessException(new IllegalArgumentException());
        }

        brokeLinks(request.getItem());

        // DO NOT change orders of executions.

        String current = request.getItem().getSeqKeyId();
        String after = request.getAfter().getSeqKeyId();
        String prev = request.getAfter().getSeqKeyId();
        String next = (request.getAfter().getNextSeqKey() != null) ?
                request.getAfter().getNextSeqKey().getSeqKeyId() : null;

        setPrev(current, prev);
        if (next != null) {
            setNext(current, next);
        }

        setNext(after, current);
        if (next != null) {
            setPrev(next, current);
        }

        return new MoveAfterResponse();
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateSeqKeyResponse updateSeqKey(UpdateSeqKeyRequest request) throws ScoreDataAccessException {
        SeqKey seqKey = request.getSeqKey();

        UpdateSetStep step = dslContext().update(SEQ_KEY);
        SeqKey prev = seqKey.getPrevSeqKey();
        if (prev == null) {
            step = step.setNull(SEQ_KEY.PREV_SEQ_KEY_ID);
        } else {
            step = step.set(SEQ_KEY.PREV_SEQ_KEY_ID, prev.getSeqKeyId());
        }

        SeqKey next = seqKey.getNextSeqKey();
        if (next == null) {
            step = step.setNull(SEQ_KEY.NEXT_SEQ_KEY_ID);
        } else {
            step = step.set(SEQ_KEY.NEXT_SEQ_KEY_ID, next.getSeqKeyId());
        }

        int affectedRows = 0;
        if (step instanceof UpdateWhereStep) {
            affectedRows = ((UpdateWhereStep) step)
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKey.getSeqKeyId()))
                    .execute();
        }

        return new UpdateSeqKeyResponse((affectedRows == 0) ? null : seqKey.getSeqKeyId());
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteSeqKeyResponse deleteSeqKey(DeleteSeqKeyRequest request) throws ScoreDataAccessException {
        String seqKeyId = request.getSeqKeyId();
        SeqKeyRecord seqKeyRecord = dslContext().selectFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKeyId))
                .fetchOne();

        // disconnect links between prev and next
        {
            SeqKeyRecord prev = dslContext().selectFrom(SEQ_KEY)
                    .where(SEQ_KEY.PREV_SEQ_KEY_ID.eq(seqKeyId))
                    .fetchOptional().orElse(null);

            SeqKeyRecord next = dslContext().selectFrom(SEQ_KEY)
                    .where(SEQ_KEY.NEXT_SEQ_KEY_ID.eq(seqKeyId))
                    .fetchOptional().orElse(null);

            SeqKeyRecord current = dslContext().selectFrom(SEQ_KEY)
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKeyId))
                    .fetchOptional().orElse(null);

            if (prev != null) {
                dslContext().update(SEQ_KEY)
                        .set(SEQ_KEY.PREV_SEQ_KEY_ID, current.getPrevSeqKeyId())
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(prev.getSeqKeyId()))
                        .execute();
            }

            if (next != null) {
                dslContext().update(SEQ_KEY)
                        .set(SEQ_KEY.NEXT_SEQ_KEY_ID, current.getNextSeqKeyId())
                        .where(SEQ_KEY.SEQ_KEY_ID.eq(next.getSeqKeyId()))
                        .execute();
            }
        }

        if (seqKeyRecord.getAsccManifestId() != null) {
            dslContext().update(ASCC_MANIFEST)
                    .setNull(ASCC_MANIFEST.SEQ_KEY_ID)
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(seqKeyRecord.getAsccManifestId()))
                    .execute();
        } else if (seqKeyRecord.getBccManifestId() != null) {
            dslContext().update(BCC_MANIFEST)
                    .setNull(BCC_MANIFEST.SEQ_KEY_ID)
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(seqKeyRecord.getBccManifestId()))
                    .execute();
        }

        int affectedRows = dslContext().deleteFrom(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(seqKeyId))
                .execute();

        return new DeleteSeqKeyResponse((affectedRows == 0) ? null : seqKeyId);
    }

}
