package org.oagi.srt.repo.component.bbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BbiepRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpRecord;
import org.oagi.srt.entity.jooq.tables.records.DtRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.bccp.BccpReadRepository;
import org.oagi.srt.repo.component.dt.DtReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.BBIEP;

@Repository
public class BbiepReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BccpReadRepository bccpReadRepository;

    @Autowired
    private DtReadRepository dtReadRepository;

    private BbiepRecord getBbiepByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(BBIEP)
                .where(and(
                        BBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        BBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public BbiepNode getBbiepNode(BigInteger topLevelAbieId, BigInteger bccpManifestId, String hashPath) {
        BccpRecord bccpRecord = bccpReadRepository.getBccpByManifestId(bccpManifestId);
        if (bccpRecord == null) {
            return null;
        }

        BbiepNode bbiepNode = new BbiepNode();

        BbiepNode.Bccp bccp = bbiepNode.getBccp();
        bccp.setBccpManifestId(bccpManifestId);
        bccp.setGuid(bccpRecord.getGuid());
        bccp.setPropertyTerm(bccpRecord.getPropertyTerm());
        bccp.setDen(bccpRecord.getDen());
        bccp.setDefinition(bccpRecord.getDefinition());
        bccp.setState(CcState.valueOf(bccpRecord.getState()));
        bccp.setNillable(bccpRecord.getIsNillable() == 1 ? true : false);
        bccp.setDefaultValue(bccpRecord.getDefaultValue());
        bccp.setFixedValue(bccpRecord.getFixedValue());

        DtRecord bdtRecord = dtReadRepository.getDtByBccpManifestId(bccpManifestId);

        BbiepNode.Bdt bdt = bbiepNode.getBdt();
        bdt.setGuid(bdtRecord.getGuid());
        bdt.setDataTypeTerm(bdtRecord.getDataTypeTerm());
        bdt.setDen(bdtRecord.getDen());
        bdt.setDefinition(bdtRecord.getDefinition());
        bdt.setState(CcState.valueOf(bdtRecord.getState()));

        BbiepNode.Bbiep bbiep = bbiepNode.getBbiep();
        bbiep.setUsed(true);
        bbiep.setHashPath(hashPath);
        bbiep.setBasedBccpManifestId(bccp.getBccpManifestId());

        BbiepRecord bbiepRecord = getBbiepByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (bbiepRecord != null) {
            bbiep.setBbiepId(bbiepRecord.getBbiepId().toBigInteger());
            bbiep.setGuid(bbiepRecord.getGuid());
            bbiep.setRemark(bbiepRecord.getRemark());
            bbiep.setBizTerm(bbiepRecord.getBizTerm());
            bbiep.setDefinition(bbiepRecord.getDefinition());
        }

        return bbiepNode;
    }
    
}
