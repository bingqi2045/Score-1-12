package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class ManifestRepository {

    @Autowired
    private DSLContext dslContext;

    public AccManifestRecord getAccManifestById(ULong manifestId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public AsccpManifestRecord getAsccpManifestById(ULong manifestId) {
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public BccpManifestRecord getBccpManifestById(ULong manifestId) {
        return dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public List<AsccpManifestRecord> getAsccpManifestByRoleOfAccManifestId(ULong roleOfAccManifestId) {
        return dslContext.select(ASCCP_MANIFEST.fields())
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(roleOfAccManifestId))
                .fetchInto(AsccpManifestRecord.class);
    }

    public List<AccManifestRecord> getAccManifestByBasedAccManifestId(ULong basedAccManifestId) {
        return dslContext.select(ACC_MANIFEST.fields())
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(basedAccManifestId))
                .fetchInto(AccManifestRecord.class);
    }

    public AsccManifestRecord getAsccManifestById(long manifestId) {
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public BccManifestRecord getBccManifestById(long manifestId) {
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public DtManifestRecord getDtManifestById(long manifestId) {
        return dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public List<AsccManifestRecord> getAsccManifestByToAsccpManifestId(ULong toAsccpManifestId) {
        return dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(toAsccpManifestId))
                .fetchInto(AsccManifestRecord.class);
    }

    public List<BccManifestRecord> getBccManifestByToBccpId(ULong toBccpId, ULong releaseId) {
        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .join(BCCP_MANIFEST)
                .on(BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .where(and(
                        BCCP_MANIFEST.BCCP_ID.eq(toBccpId),
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId)
                ))
                .fetchInto(BccManifestRecord.class);
    }

    public List<AsccManifestRecord> getAsccManifestByFromAccManifestId(ULong fromAccManifestId) {
        return dslContext.select(ASCC_MANIFEST.fields())
                .from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId))
                .fetchInto(AsccManifestRecord.class);
    }

    public List<BccManifestRecord> getBccManifestByFromAccManifestId(ULong fromAccManifestId) {
        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(fromAccManifestId))
                .fetchInto(BccManifestRecord.class);
    }

    public void deleteAccManifestById(long manifestId) {
        dslContext.deleteFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteAsccpManifestById(long manifestId) {
        dslContext.deleteFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteBccpManifestById(long manifestId) {
        dslContext.deleteFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public boolean isAccUsed(long accId) {
        return dslContext.selectCount()
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isAsccpUsed(long asccpId) {
        return dslContext.selectCount()
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isBccpUsed(long bccpId) {
        return dslContext.selectCount()
                .from(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isAsccUsed(long asccId) {
        return dslContext.selectCount()
                .from(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_ID.eq(ULong.valueOf(asccId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isBccUsed(long bccId) {
        return dslContext.selectCount()
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

}
