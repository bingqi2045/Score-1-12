package org.oagi.srt.repo.component.bbie_sc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BbieScRecord;
import org.oagi.srt.entity.jooq.tables.records.DtScRecord;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUsed;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.dt_sc.DtScReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BbieScReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private DtScReadRepository dtScReadRepository;

    private BbieScRecord getBbieScByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(BBIE_SC)
                .where(and(
                        BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BBIE_SC.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public BbieScNode getBbieScNode(BigInteger topLevelAbieId, BigInteger dtScManifestId, String hashPath) {
        DtScRecord dtScRecord = dtScReadRepository.getDtScByManifestId(dtScManifestId);
        if (dtScRecord == null) {
            return null;
        }

        BbieScNode bbieScNode = new BbieScNode();

        BbieScNode.BdtSc bdtSc = bbieScNode.getBdtSc();
        bdtSc.setDtScManifestId(dtScManifestId);
        bdtSc.setGuid(dtScRecord.getGuid());
        bdtSc.setCardinalityMin(dtScRecord.getCardinalityMin());
        bdtSc.setCardinalityMax(dtScRecord.getCardinalityMax());
        bdtSc.setPropertyTerm(dtScRecord.getPropertyTerm());
        bdtSc.setRepresentationTerm(dtScRecord.getRepresentationTerm());
        bdtSc.setDefinition(dtScRecord.getDefinition());
        bdtSc.setDefaultValue(dtScRecord.getDefaultValue());
        bdtSc.setFixedValue(dtScRecord.getFixedValue());
        bdtSc.setState(CcState.valueOf(
                dslContext.select(DT.STATE)
                        .from(DT)
                        .where(DT.DT_ID.eq(dtScRecord.getOwnerDtId()))
                        .fetchOneInto(String.class)));

        BbieScNode.BbieSc bbieSc = getBbieSc(topLevelAbieId, hashPath);
        bbieScNode.setBbieSc(bbieSc);

        if (bbieSc.getBbieScId() == null) {
            bbieSc.setBasedDtScManifestId(bdtSc.getDtScManifestId());
            bbieSc.setCardinalityMin(dtScRecord.getCardinalityMin());
            bbieSc.setCardinalityMax(dtScRecord.getCardinalityMax());
            bbieSc.setDefaultValue(dtScRecord.getDefaultValue());
            bbieSc.setFixedValue(dtScRecord.getFixedValue());
        }

        return bbieScNode;
    }

    public BbieScNode.BbieSc getBbieSc(BigInteger topLevelAbieId, String hashPath) {
        BbieScNode.BbieSc bbieSc = new BbieScNode.BbieSc();
        bbieSc.setHashPath(hashPath);

        BbieScRecord bbieScRecord = getBbieScByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (bbieScRecord != null) {
            bbieSc.setBbieScId(bbieScRecord.getBbieScId().toBigInteger());
            bbieSc.setBbieHashPath(dslContext.select(BBIE.HASH_PATH)
                    .from(BBIE)
                    .where(and(
                            BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                            BBIE.BBIE_ID.eq(bbieScRecord.getBbieId())
                    ))
                    .fetchOneInto(String.class));
            bbieSc.setBasedDtScManifestId(bbieScRecord.getBasedDtScManifestId().toBigInteger());
            bbieSc.setUsed(bbieScRecord.getIsUsed() == 1 ? true : false);
            bbieSc.setGuid(bbieScRecord.getGuid());
            bbieSc.setCardinalityMin(bbieScRecord.getCardinalityMin());
            bbieSc.setCardinalityMax(bbieScRecord.getCardinalityMax());
            bbieSc.setRemark(bbieScRecord.getRemark());
            bbieSc.setBizTerm(bbieScRecord.getBizTerm());
            bbieSc.setDefinition(bbieScRecord.getDefinition());
            bbieSc.setDefaultValue(bbieScRecord.getDefaultValue());
            bbieSc.setFixedValue(bbieScRecord.getFixedValue());
            bbieSc.setExample(bbieScRecord.getExample());

            bbieSc.setBdtScPriRestriId((bbieScRecord.getDtScPriRestriId() != null) ?
                    bbieScRecord.getDtScPriRestriId().toBigInteger() : null);
            bbieSc.setCodeListId((bbieScRecord.getCodeListId() != null) ?
                    bbieScRecord.getCodeListId().toBigInteger() : null);
            bbieSc.setAgencyIdListId((bbieScRecord.getAgencyIdListId() != null) ?
                    bbieScRecord.getAgencyIdListId().toBigInteger() : null);
        }

        return bbieSc;
    }

    public List<BieEditUsed> getUsedBbieScList(BigInteger topLevelAbieId) {
        return dslContext.select(BBIE_SC.HASH_PATH)
                .from(BBIE_SC)
                .where(and(
                        BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BBIE_SC.IS_USED.eq((byte) 1)
                ))
                .fetchStream().map(record -> {
                    BieEditUsed bieEditUsed = new BieEditUsed();
                    bieEditUsed.setType("BBIE_SC");
                    bieEditUsed.setHashPath(record.get(BBIE_SC.HASH_PATH));
                    bieEditUsed.setTopLevelAbieId(topLevelAbieId);
                    bieEditUsed.setUsed(true);
                    return bieEditUsed;
                })
                .collect(Collectors.toList());
    }

}
