package org.oagi.srt.repo.component.bbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BbieRecord;
import org.oagi.srt.entity.jooq.tables.records.BccRecord;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUsed;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.bcc.BccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BbieReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BccReadRepository bccReadRepository;

    private BbieRecord getBbieByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(BBIE)
                .where(and(
                        BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
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

        BbieNode.Bbie bbie = getBbie(topLevelAbieId, hashPath);
        bbieNode.setBbie(bbie);

        if (bbie.getBbieId() == null) {
            bbie.setBasedBccManifestId(bcc.getBccManifestId());
            bbie.setCardinalityMin(bccRecord.getCardinalityMin());
            bbie.setCardinalityMax(bccRecord.getCardinalityMax());
            bbie.setDefaultValue(bccRecord.getDefaultValue());
            bbie.setFixedValue(bccRecord.getFixedValue());
        }

        return bbieNode;
    }

    public BbieNode.Bbie getBbie(BigInteger topLevelAbieId, String hashPath) {
        BbieNode.Bbie bbie = new BbieNode.Bbie();
        bbie.setHashPath(hashPath);

        BbieRecord bbieRecord = getBbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (bbieRecord != null) {
            bbie.setBbieId(bbieRecord.getBbieId().toBigInteger());
            bbie.setFromAbieHashPath(dslContext.select(ABIE.HASH_PATH)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                            ABIE.ABIE_ID.eq(bbieRecord.getFromAbieId())
                    ))
                    .fetchOneInto(String.class));
            bbie.setToBbiepHashPath(dslContext.select(BBIEP.HASH_PATH)
                    .from(BBIEP)
                    .where(and(
                            BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                            BBIEP.BBIEP_ID.eq(bbieRecord.getToBbiepId())
                    ))
                    .fetchOneInto(String.class));
            bbie.setBasedBccManifestId(bbieRecord.getBasedBccManifestId().toBigInteger());
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

        return bbie;
    }

    public List<BieEditUsed> getUsedBbieList(BigInteger topLevelAsbiepId) {
        return dslContext.select(BBIEP.HASH_PATH)
                .from(BBIE)
                .join(BBIEP).on(and(
                        BBIE.TO_BBIEP_ID.eq(BBIEP.BBIEP_ID),
                        BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                ))
                .where(and(
                        BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAsbiepId)),
                        BBIE.IS_USED.eq((byte) 1)
                ))
                .fetchStream().map(record -> {
                    BieEditUsed bieEditUsed = new BieEditUsed();
                    bieEditUsed.setType("BBIEP");
                    bieEditUsed.setHashPath(record.get(BBIEP.HASH_PATH));
                    bieEditUsed.setTopLevelAsbiepId(topLevelAsbiepId);
                    bieEditUsed.setUsed(true);
                    return bieEditUsed;
                })
                .collect(Collectors.toList());
    }
}
