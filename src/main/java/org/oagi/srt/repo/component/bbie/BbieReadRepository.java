package org.oagi.srt.repo.component.bbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BbieRecord;
import org.oagi.srt.entity.jooq.tables.records.BccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.bcc.BccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.BBIE;

@Repository
public class BbieReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BccReadRepository bccReadRepository;

    private BbieRecord getBbieByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(BBIE)
                .where(and(
                        BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BBIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public BbieNode getBbieNode(BigInteger topLevelAbieId, BigInteger bccManifestId, String hashPath) {
        BccRecord bccRecord = bccReadRepository.getBccByManifestId(bccManifestId);
        if (bccRecord == null) {
            return null;
        }

        BbieNode bbieNode = new BbieNode();

        BbieNode.Bcc bcc = bbieNode.getBcc();
        bcc.setBccManifestId(bccManifestId);
        bcc.setGuid(bccRecord.getGuid());
        bcc.setCardinalityMin(bccRecord.getCardinalityMin());
        bcc.setCardinalityMax(bccRecord.getCardinalityMax());
        bcc.setDen(bccRecord.getDen());
        bcc.setDefinition(bccRecord.getDefinition());
        bcc.setState(CcState.valueOf(bccRecord.getState()));
        bcc.setDefaultValue(bccRecord.getDefaultValue());
        bcc.setFixedValue(bccRecord.getFixedValue());
        bcc.setDeprecated(bccRecord.getIsDeprecated() == 1 ? true : false);
        bcc.setNillable(bccRecord.getIsNillable() == 1 ? true : false);

        BbieRecord bbieRecord = getBbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (bbieRecord != null) {
            BbieNode.Bbie bbie = bbieNode.getBbie();
            bbie.setBbieId(bbieRecord.getBbieId().toBigInteger());
            bbie.setUsed(bbieRecord.getIsUsed() == 1 ? true : false);
            bbie.setGuid(bbieRecord.getGuid());
            bbie.setCardinalityMin(bbieRecord.getCardinalityMin());
            bbie.setCardinalityMax(bbieRecord.getCardinalityMax());
            bbie.setNillable(bbieRecord.getIsNillable() == 1 ? true : false);
            bbie.setRemark(bbieRecord.getRemark());
            bbie.setDefinition(bbieRecord.getDefinition());
            bbie.setDefaultValue(bbieRecord.getDefaultValue());
            bbie.setFixedValue(bbieRecord.getFixedValue());
            bbie.setExample(bbieRecord.getExample());

            bbie.setBdtPriRestriId((bbieRecord.getBdtPriRestriId() != null) ?
                    bbieRecord.getBdtPriRestriId().toBigInteger() : null);
            bbie.setCodeListId((bbieRecord.getCodeListId() != null) ?
                    bbieRecord.getCodeListId().toBigInteger() : null);
            bbie.setAgencyIdListId((bbieRecord.getAgencyIdListId() != null) ?
                    bbieRecord.getAgencyIdListId().toBigInteger() : null);
        }

        return bbieNode;
    }
    
}
