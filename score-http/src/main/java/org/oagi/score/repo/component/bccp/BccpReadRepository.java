package org.oagi.score.repo.component.bccp;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccpManifestRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccpRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class BccpReadRepository {

    @Autowired
    private DSLContext dslContext;

    public BccpRecord getBccpByManifestId(String bccpManifestId) {
        return dslContext.select(BCCP.fields())
                .from(BCCP)
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccpManifestId))
                .fetchOptionalInto(BccpRecord.class).orElse(null);
    }

    public BccpManifestRecord getBccpManifestByManifestId(String bccpManifestId) {
        return dslContext.select(BCCP_MANIFEST.fields())
                .from(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccpManifestId))
                .fetchOptionalInto(BccpManifestRecord.class).orElse(null);
    }

    public List<String> getCdtPrimitivesByManifestId(String bccpManifestId) {
        return dslContext.selectDistinct(CDT_PRI.NAME)
                .from(BCCP_MANIFEST)
                .join(DT_MANIFEST).on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                .join(DT).on(DT_MANIFEST.DT_ID.eq(DT.DT_ID))
                .join(BDT_PRI_RESTRI).on(DT.DT_ID.eq(BDT_PRI_RESTRI.BDT_ID))
                .join(CDT_AWD_PRI_XPS_TYPE_MAP).on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(CDT_AWD_PRI).on(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_ID.eq(CDT_AWD_PRI.CDT_AWD_PRI_ID))
                .join(CDT_PRI).on(CDT_AWD_PRI.CDT_PRI_ID.eq(CDT_PRI.CDT_PRI_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccpManifestId))
                .fetchInto(String.class);
    }
}
