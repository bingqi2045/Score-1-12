package org.oagi.score.repo.component.acc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.entity.jooq.tables.records.AccRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

import static org.oagi.score.repo.entity.jooq.Tables.ACC;
import static org.oagi.score.repo.entity.jooq.Tables.ACC_MANIFEST;

@Repository
public class AccReadRepository {

    @Autowired
    private DSLContext dslContext;

    public AccRecord getAccByManifestId(BigInteger accManifestId) {
        return dslContext.select(ACC.fields())
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOptionalInto(AccRecord.class).orElse(null);
    }
}
