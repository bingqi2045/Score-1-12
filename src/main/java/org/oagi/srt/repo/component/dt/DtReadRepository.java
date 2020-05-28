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
}
