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
import static org.oagi.srt.entity.jooq.Tables.*;

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

        AsbieNode.Asbie asbie = getAsbie(topLevelAbieId, hashPath);
        if (asbie.getAsbieId() == null) {
            asbie.setBasedAsccManifestId(ascc.getAsccManifestId());
            asbie.setCardinalityMin(ascc.getCardinalityMin());
            asbie.setCardinalityMax(ascc.getCardinalityMax());
        }

        return asbieNode;
    }

    public AsbieNode.Asbie getAsbie(BigInteger topLevelAbieId, String hashPath) {
        AsbieNode.Asbie asbie = new AsbieNode.Asbie();
        asbie.setHashPath(hashPath);

        AsbieRecord asbieRecord = getAsbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (asbieRecord != null) {
            asbie.setAsbieId(asbieRecord.getAsbieId().toBigInteger());
            asbie.setGuid(asbieRecord.getGuid());
            asbie.setFromAbieHashPath(dslContext.select(ABIE.HASH_PATH)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                            ABIE.ABIE_ID.eq(asbieRecord.getFromAbieId())
                    ))
                    .fetchOneInto(String.class));
            asbie.setToAsbiepHashPath(dslContext.select(ASBIEP.HASH_PATH)
                    .from(ASBIEP)
                    .where(and(
                            ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                            ASBIEP.ASBIEP_ID.eq(asbieRecord.getToAsbiepId())
                    ))
                    .fetchOneInto(String.class));
            asbie.setBasedAsccManifestId(asbieRecord.getBasedAsccManifestId().toBigInteger());
            asbie.setUsed(asbieRecord.getIsUsed() == 1 ? true : false);
            asbie.setCardinalityMin(asbieRecord.getCardinalityMin());
            asbie.setCardinalityMax(asbieRecord.getCardinalityMax());
            asbie.setNillable(asbieRecord.getIsNillable() == 1 ? true : false);
            asbie.setRemark(asbieRecord.getRemark());
            asbie.setDefinition(asbieRecord.getDefinition());
        }

        return asbie;
    }
    
}
