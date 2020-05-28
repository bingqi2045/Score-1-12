package org.oagi.srt.repo.component.ascc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.oagi.srt.entity.jooq.Tables.ASCC;
import static org.oagi.srt.entity.jooq.Tables.ASCC_MANIFEST;

@Repository
public class AsccReadRepository {

    @Autowired
    private DSLContext dslContext;

    public AsccRecord getAsccByManifestId(BigInteger asccManifestId) {
        return dslContext.select(ASCC.fields())
                .from(ASCC)
                .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(asccManifestId)))
                .fetchOptionalInto(AsccRecord.class).orElse(null);
    }
}
