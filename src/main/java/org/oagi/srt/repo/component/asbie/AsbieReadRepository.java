package org.oagi.srt.repo.component.asbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsbieRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.ascc.AsccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ASBIE;

@Repository
public class AsbieReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private AsccReadRepository asccReadRepository;

    private AsbieRecord getAsbieByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(ASBIE)
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public AsbieNode getAsbieNode(BigInteger topLevelAbieId, BigInteger asccManifestId, String hashPath) {
        AsccRecord asccRecord = asccReadRepository.getAsccByManifestId(asccManifestId);
        if (asccRecord == null) {
            return null;
        }

        AsbieNode asbieNode = new AsbieNode();

        AsbieNode.Ascc ascc = asbieNode.getAscc();
        ascc.setAsccManifestId(asccManifestId);
        ascc.setGuid(asccRecord.getGuid());
        ascc.setCardinalityMin(asccRecord.getCardinalityMin());
        ascc.setCardinalityMax(asccRecord.getCardinalityMax());
        ascc.setDen(asccRecord.getDen());
        ascc.setDefinition(asccRecord.getDefinition());
        ascc.setState(CcState.valueOf(asccRecord.getState()));

        AsbieNode.Asbie asbie = asbieNode.getAsbie();
        asbie.setHashPath(hashPath);
        asbie.setBasedAsccManifestId(ascc.getAsccManifestId());

        AsbieRecord asbieRecord = getAsbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (asbieRecord != null) {
            asbie.setAsbieId(asbieRecord.getAsbieId().toBigInteger());
            asbie.setUsed(asbieRecord.getIsUsed() == 1 ? true : false);
            asbie.setGuid(asbieRecord.getGuid());
            asbie.setCardinalityMin(asbieRecord.getCardinalityMin());
            asbie.setCardinalityMax(asbieRecord.getCardinalityMax());
            asbie.setNillable(asbieRecord.getIsNillable() == 1 ? true : false);
            asbie.setRemark(asbieRecord.getRemark());
            asbie.setDefinition(asbieRecord.getDefinition());
        }

        return asbieNode;
    }
    
}
