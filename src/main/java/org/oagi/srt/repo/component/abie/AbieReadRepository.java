package org.oagi.srt.repo.component.abie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AbieRecord;
import org.oagi.srt.entity.jooq.tables.records.AccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.acc.AccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ABIE;
import static org.oagi.srt.entity.jooq.Tables.TOP_LEVEL_ABIE;

@Repository
public class AbieReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private AccReadRepository accReadRepository;

    private AbieRecord getAbieByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(ABIE)
                .where(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ABIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public AbieNode getAbieNode(BigInteger topLevelAbieId, BigInteger accManifestId, String hashPath) {
        AccRecord accRecord = accReadRepository.getAccByManifestId(accManifestId);
        if (accRecord == null) {
            return null;
        }

        AbieNode abieNode = new AbieNode();

        AbieNode.Acc acc = abieNode.getAcc();
        acc.setAccManifestId(accManifestId);
        acc.setGuid(accRecord.getGuid());
        acc.setObjectClassTerm(accRecord.getObjectClassTerm());
        acc.setDen(accRecord.getObjectClassTerm() + ". Details");
        acc.setDefinition(accRecord.getDefinition());
        acc.setState(CcState.valueOf(accRecord.getState()));

        AbieNode.Abie abie = getAbie(topLevelAbieId, hashPath);
        abieNode.setAbie(abie);

        if (abie.getAbieId() == null) {
            abie.setBasedAccManifestId(acc.getAccManifestId());
        }

        return abieNode;
    }

    public AbieNode.Abie getAbie(BigInteger topLevelAbieId, String hashPath) {
        AbieNode.Abie abie = new AbieNode.Abie();
        abie.setUsed(true);
        abie.setHashPath(hashPath);

        AbieRecord abieRecord = getAbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (abieRecord != null) {
            abie.setAbieId(abieRecord.getAbieId().toBigInteger());
            abie.setGuid(abieRecord.getGuid());
            abie.setBasedAccManifestId(abieRecord.getBasedAccManifestId().toBigInteger());
            abie.setVersion(abieRecord.getVersion());
            if (abieRecord.getClientId() != null) {
                abie.setClientId(abieRecord.getClientId().toBigInteger());
            }
            abie.setStatus(abieRecord.getStatus());
            abie.setRemark(abieRecord.getRemark());
            abie.setBizTerm(abieRecord.getBizTerm());
            abie.setDefinition(abieRecord.getDefinition());
        }

        return abie;
    }

    public AbieNode.Abie getAbieByTopLevelAbieId(BigInteger topLevelAbieId) {
        String abieHashPath = dslContext.select(ABIE.HASH_PATH)
                .from(ABIE)
                .join(TOP_LEVEL_ABIE).on(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID),
                        ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID)
                ))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOneInto(String.class);
        return getAbie(topLevelAbieId, abieHashPath);
    }
}
