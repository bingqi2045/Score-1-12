package org.oagi.srt.repo.component.asccp;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsccpRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.oagi.srt.entity.jooq.Tables.ASCCP;
import static org.oagi.srt.entity.jooq.Tables.ASCCP_MANIFEST;

@Repository
public class AsccpReadRepository {
    
    @Autowired
    private DSLContext dslContext;

    public AsccpRecord getAsccpByManifestId(BigInteger asccpManifestId) {
        return dslContext.select(ASCCP.fields())
                .from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(asccpManifestId)))
                .fetchOptionalInto(AsccpRecord.class).orElse(null);
    }
}