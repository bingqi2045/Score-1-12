package org.oagi.score.gateway.http.api.cc_management.repository;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class ManifestRepository {

    @Autowired
    private DSLContext dslContext;

    public AccManifestRecord getAccManifestById(String manifestId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public AsccpManifestRecord getAsccpManifestById(String manifestId) {
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public BccpManifestRecord getBccpManifestById(String manifestId) {
        return dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public AsccManifestRecord getAsccManifestById(String manifestId) {
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public BccManifestRecord getBccManifestById(String manifestId) {
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public DtManifestRecord getDtManifestById(String manifestId) {
        return dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public List<BccManifestRecord> getBccManifestByFromAccManifestId(String fromAccManifestId) {
        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId))
                .fetchInto(BccManifestRecord.class);
    }

}
