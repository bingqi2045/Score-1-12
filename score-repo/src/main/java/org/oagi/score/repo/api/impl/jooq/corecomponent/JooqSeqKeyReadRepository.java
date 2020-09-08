package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.BccEntityType;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyReadRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyRequest;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyResponse;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKey;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKeyType;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.security.AccessControl;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BCC;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.SEQ_KEY;
import static org.oagi.score.repo.api.impl.jooq.utils.DSLUtils.isNull;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqSeqKeyReadRepository
        extends JooqScoreRepository
        implements SeqKeyReadRepository {

    public JooqSeqKeyReadRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public GetSeqKeyResponse getSeqKey(GetSeqKeyRequest request) throws ScoreDataAccessException {
        BigInteger fromAccId = null;
        if (!isNull(request.getSeqKeyId())) {
            fromAccId = dslContext().select(SEQ_KEY.FROM_ACC_ID)
                    .from(SEQ_KEY)
                    .where(SEQ_KEY.SEQ_KEY_ID.eq(ULong.valueOf(request.getSeqKeyId())))
                    .fetchOptionalInto(BigInteger.class).orElse(null);
        } else if (!isNull(request.getFromAccId())) {
            fromAccId = request.getFromAccId();
        }

        if (fromAccId == null) {
            throw new ScoreDataAccessException(new IllegalArgumentException());
        }

        SeqKey seqKey = null;
        Map<ULong, Record> seqKeyRecordMap = dslContext()
                .select(SEQ_KEY.SEQ_KEY_ID, SEQ_KEY.TYPE, SEQ_KEY.CC_ID, SEQ_KEY.FROM_ACC_ID,
                        SEQ_KEY.PREV_SEQ_KEY_ID, SEQ_KEY.NEXT_SEQ_KEY_ID,
                        BCC.ENTITY_TYPE)
                .from(SEQ_KEY)
                .leftJoin(BCC).on(SEQ_KEY.CC_ID.eq(BCC.BCC_ID))
                .where(SEQ_KEY.FROM_ACC_ID.eq(ULong.valueOf(request.getFromAccId())))
                .fetchStream()
                .collect(Collectors.toMap(e -> e.get(SEQ_KEY.SEQ_KEY_ID), Function.identity()));

        if (!seqKeyRecordMap.isEmpty()) {
            Record node = seqKeyRecordMap.values().stream()
                    .filter(e -> e.get(SEQ_KEY.PREV_SEQ_KEY_ID) == null)
                    .findAny().orElseThrow(() -> new IllegalStateException());

            seqKey = mapper(seqKeyRecordMap, node);

            if (!isNull(request.getSeqKeyId())) {
                while (seqKey != null && !request.getSeqKeyId().equals(seqKey.getSeqKeyId())) {
                    seqKey = seqKey.getNextSeqKey();
                }
            }
        }

        return new GetSeqKeyResponse(seqKey);
    }

    private SeqKey mapper(Map<ULong, Record> seqKeyRecordMap, Record node) {
        return mapper(seqKeyRecordMap, node, null);
    }

    private SeqKey mapper(Map<ULong, Record> seqKeyRecordMap, Record node, SeqKey prev) {
        SeqKey seqKey = new SeqKey();
        seqKey.setSeqKeyId(node.get(SEQ_KEY.SEQ_KEY_ID).toBigInteger());
        seqKey.setFromAccId(node.get(SEQ_KEY.FROM_ACC_ID).toBigInteger());
        seqKey.setSeqKeyType(SeqKeyType.valueOf(node.get(SEQ_KEY.TYPE).toString().toUpperCase()));
        seqKey.setCcId(node.get(SEQ_KEY.CC_ID).toBigInteger());
        if (SeqKeyType.BCC == seqKey.getSeqKeyType()) {
            seqKey.setEntityType(BccEntityType.valueOf(node.get(BCC.ENTITY_TYPE)));
        }
        seqKey.setPrevSeqKey(prev);

        node = seqKeyRecordMap.get(node.get(SEQ_KEY.NEXT_SEQ_KEY_ID));
        if (node != null) {
            SeqKey next = mapper(seqKeyRecordMap, node, seqKey);
            seqKey.setNextSeqKey(next);
        }

        return seqKey;
    }
}
