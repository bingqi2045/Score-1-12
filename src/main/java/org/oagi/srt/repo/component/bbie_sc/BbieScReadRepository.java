package org.oagi.srt.repo.component.bbie_sc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BbieScRecord;
import org.oagi.srt.entity.jooq.tables.records.DtScRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.dt_sc.DtScReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.BBIE_SC;
import static org.oagi.srt.entity.jooq.Tables.DT;

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

        BbieScNode.DtSc dtSc = bbieScNode.getDtSc();
        dtSc.setDtScManifestId(dtScManifestId);
        dtSc.setGuid(dtScRecord.getGuid());
        dtSc.setCardinalityMin(dtScRecord.getCardinalityMin());
        dtSc.setCardinalityMax(dtScRecord.getCardinalityMax());
        dtSc.setPropertyTerm(dtScRecord.getPropertyTerm());
        dtSc.setRepresentationTerm(dtScRecord.getRepresentationTerm());
        dtSc.setDefinition(dtScRecord.getDefinition());
        dtSc.setState(CcState.valueOf(
                dslContext.select(DT.STATE)
                        .from(DT)
                        .where(DT.DT_ID.eq(dtScRecord.getOwnerDtId()))
                        .fetchOneInto(String.class)));

        BbieScRecord bbieScRecord = getBbieScByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (bbieScRecord != null) {
            BbieScNode.BbieSc bbieSc = bbieScNode.getBbieSc();
            bbieSc.setBbieScId(bbieScRecord.getBbieScId().toBigInteger());
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

        return bbieScNode;
    }

}
