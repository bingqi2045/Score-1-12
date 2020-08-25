package org.oagi.score.repo.component.bcc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.tables.records.BccRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.oagi.score.entity.jooq.Tables.BCC;
import static org.oagi.score.entity.jooq.Tables.BCC_MANIFEST;

@Repository
public class BccReadRepository {

    @Autowired
    private DSLContext dslContext;

    public BccRecord getBccByManifestId(BigInteger bccManifestId) {
        return dslContext.select(BCC.fields())
                .from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                .fetchOptionalInto(BccRecord.class).orElse(null);
    }
}
