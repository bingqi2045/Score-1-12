package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyReadRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyRequest;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyResponse;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKey;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKeyType;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.SeqKeyRecord;
import org.oagi.score.repo.api.security.AccessControl;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        SeqKey seqKey = null;
        BigInteger fromAccId = request.getFromAccId();
        if (!isNull(fromAccId)) {
            Map<ULong, SeqKeyRecord> seqKeyRecordMap = dslContext()
                    .selectFrom(SEQ_KEY)
                    .where(SEQ_KEY.FROM_ACC_ID.eq(ULong.valueOf(fromAccId)))
                    .fetchStream()
                    .collect(Collectors.toMap(SeqKeyRecord::getSeqKeyId, Function.identity()));

            SeqKeyRecord node = seqKeyRecordMap.values().stream()
                    .filter(e -> e.getPrevSeqKeyId() == null)
                    .findAny().orElseThrow(() -> new IllegalStateException());

            seqKey = mapper(seqKeyRecordMap, node);
        }

        return new GetSeqKeyResponse(seqKey);
    }

    private SeqKey mapper(Map<ULong, SeqKeyRecord> seqKeyRecordMap, SeqKeyRecord node) {
        return mapper(seqKeyRecordMap, node, null);
    }

    private SeqKey mapper(Map<ULong, SeqKeyRecord> seqKeyRecordMap, SeqKeyRecord node, SeqKey prev) {
        SeqKey seqKey = new SeqKey();
        seqKey.setSeqKeyId(node.getSeqKeyId().toBigInteger());
        seqKey.setFromAccId(node.getFromAccId().toBigInteger());
        seqKey.setSeqKeyType(SeqKeyType.valueOf(node.getType().getName()));
        seqKey.setCcId(node.getCcId().toBigInteger());
        seqKey.setPrevSeqKey(prev);

        node = seqKeyRecordMap.get(node.getNextSeqKeyId());
        if (node != null) {
            SeqKey next = mapper(seqKeyRecordMap, node, seqKey);
            seqKey.setNextSeqKey(next);
        }

        return seqKey;
    }
}
