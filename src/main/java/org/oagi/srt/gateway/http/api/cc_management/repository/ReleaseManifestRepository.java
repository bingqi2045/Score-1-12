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
public class ReleaseManifestRepository {

    @Autowired
    private DSLContext dslContext;

    public AccManifestRecord getAccReleaseManifestById(ULong manifestId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public AsccpManifestRecord getAsccpReleaseManifestById(ULong manifestId) {
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public BccpManifestRecord getBccpReleaseManifestById(ULong manifestId) {
        return dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(manifestId))
                .fetchOne();
    }

    public List<AsccpManifestRecord> getAsccpReleaseManifestByRoleOfAccId(long roleOfAccId, long releaseId) {
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(and(ASCCP_MANIFEST.ROLE_OF_ACC_ID.eq(ULong.valueOf(roleOfAccId)),
                        ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetch();
    }

    public List<AccManifestRecord> getAccReleaseManifestByBasedAccId(long basedAccId, long releaseId) {
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(and(ACC_MANIFEST.BASED_ACC_ID.eq(ULong.valueOf(basedAccId)),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetch();
    }

    public AsccManifestRecord getAsccReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public BccManifestRecord getBccReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public DtManifestRecord getDtReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public List<AsccManifestRecord> getAsccReleaseManifestByToAsccpId(ULong toAsccpId, ULong releaseId) {
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(and(ASCC_MANIFEST.TO_ASCCP_ID.eq(toAsccpId),
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<BccManifestRecord> getBccReleaseManifestByToBccpId(ULong toBccpId, ULong releaseId) {
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(and(BCC_MANIFEST.TO_BCCP_ID.eq(toBccpId),
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<AsccManifestRecord> getAsccReleaseManifestByFromAccId(ULong fromAccId, ULong releaseId) {
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(and(ASCC_MANIFEST.FROM_ACC_ID.eq(fromAccId),
                        ASCC_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<BccManifestRecord> getBccReleaseManifestByFromAccId(ULong fromAccId, ULong releaseId) {
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(and(BCC_MANIFEST.FROM_ACC_ID.eq(fromAccId),
                        BCC_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public void deleteAccReleaseManifestById(long manifestId) {
        dslContext.deleteFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteAsccpReleaseManifestById(long manifestId) {
        dslContext.deleteFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteBccpReleaseManifestById(long manifestId) {
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
