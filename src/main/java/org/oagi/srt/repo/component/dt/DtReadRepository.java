package org.oagi.srt.repo.component.dt;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.DtRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class DtReadRepository {
    
    @Autowired
    private DSLContext dslContext;
    
    public DtRecord getDtByBccpManifestId(BigInteger bccpManifestId) {
        return dslContext.select(DT.fields())
                .from(DT)
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .join(BCCP_MANIFEST).on(DT_MANIFEST.DT_MANIFEST_ID.eq(BCCP_MANIFEST.BDT_MANIFEST_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchOptionalInto(DtRecord.class).orElse(null);
    }

    public DtRecord getDtByDtManifestId(BigInteger dtManifestId) {
        return dslContext.select(DT.fields())
                .from(DT)
                .join(DT_MANIFEST).on(DT.DT_ID.eq(DT_MANIFEST.DT_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(dtManifestId)))
                .fetchOptionalInto(DtRecord.class).orElse(null);
    }

    public BdtNode getBdtNode(BigInteger topLevelAbieId, BigInteger dtManifestId) {
        DtRecord dtRecord = getDtByDtManifestId(dtManifestId);
        if (dtRecord == null) {
            return null;
        }

        BdtNode bdtNode = new BdtNode();

        bdtNode.setDataTypeTerm(dtRecord.getDataTypeTerm());
        bdtNode.setQualifier(dtRecord.getQualifier());
        bdtNode.setDefinition(dtRecord.getDefinition());
        bdtNode.setDen(dtRecord.getDen());

        return bdtNode;
    }
}
