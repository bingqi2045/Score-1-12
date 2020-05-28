package org.oagi.srt.repo.component.asbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.asccp.AsccpReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ASBIEP;

@Repository
public class AsbiepReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private AsccpReadRepository asccpReadRepository;

    private AsbiepRecord getAsbiepByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public AsbiepNode getAsbiepNode(BigInteger topLevelAbieId, BigInteger asccpManifestId, String hashPath) {
        AsccpRecord asccpRecord = asccpReadRepository.getAsccpByManifestId(asccpManifestId);
        if (asccpRecord == null) {
            return null;
        }

        AsbiepNode asbiepNode = new AsbiepNode();

        AsbiepNode.Asccp asccp = asbiepNode.getAsccp();
        asccp.setAsccpManifestId(asccpManifestId);
        asccp.setGuid(asccpRecord.getGuid());
        asccp.setPropertyTerm(asccpRecord.getPropertyTerm());
        asccp.setDen(asccpRecord.getDen());
        asccp.setDefinition(asccpRecord.getDefinition());
        asccp.setState(CcState.valueOf(asccpRecord.getState()));
        asccp.setNillable(asccpRecord.getIsNillable() == 1 ? true : false);

        AsbiepRecord asbiepRecord = getAsbiepByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (asbiepRecord != null) {
            AsbiepNode.Asbiep asbiep = asbiepNode.getAsbiep();
            asbiep.setAsbiepId(asbiepRecord.getAsbiepId().toBigInteger());
            asbiep.setUsed(true);
            asbiep.setGuid(asbiepRecord.getGuid());
            asbiep.setRemark(asbiepRecord.getRemark());
            asbiep.setBizTerm(asbiepRecord.getBizTerm());
            asbiep.setDefinition(asbiepRecord.getDefinition());
        }

        return asbiepNode;
    }
    
}
