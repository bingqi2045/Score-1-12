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

    public AccReleaseManifestRecord getAccReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public AsccpReleaseManifestRecord getAsccpReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public List<AsccpReleaseManifestRecord> getAsccpReleaseManifestByRoleOfAccId(long roleOfAccId, long releaseId) {
        return dslContext.selectFrom(ASCCP_RELEASE_MANIFEST)
                .where(and(ASCCP_RELEASE_MANIFEST.ROLE_OF_ACC_ID.eq(ULong.valueOf(roleOfAccId)),
                        ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetch();
    }

    public List<AccReleaseManifestRecord> getAccReleaseManifestByBasedAccId(long basedAccId, long releaseId) {
        return dslContext.selectFrom(ACC_RELEASE_MANIFEST)
                .where(and(ACC_RELEASE_MANIFEST.BASED_ACC_ID.eq(ULong.valueOf(basedAccId)),
                        ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .fetch();
    }

    public BccpReleaseManifestRecord getBccpReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public AsccReleaseManifestRecord getAsccReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.ASCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public BccReleaseManifestRecord getBccReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.BCC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public DtReleaseManifestRecord getDtReleaseManifestById(long manifestId) {
        return dslContext.selectFrom(DT_RELEASE_MANIFEST)
                .where(DT_RELEASE_MANIFEST.DT_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .fetchOne();
    }

    public List<AsccReleaseManifestRecord> getAsccReleaseManifestByToAsccpId(ULong toAsccpId, ULong releaseId) {
        return dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.TO_ASCCP_ID.eq(toAsccpId),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<BccReleaseManifestRecord> getBccReleaseManifestByToBccpId(ULong toBccpId, ULong releaseId) {
        return dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.TO_BCCP_ID.eq(toBccpId),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<AsccReleaseManifestRecord> getAsccReleaseManifestByFromAccId(ULong fromAccId, ULong releaseId) {
        return dslContext.selectFrom(ASCC_RELEASE_MANIFEST)
                .where(and(ASCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(fromAccId),
                        ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public List<BccReleaseManifestRecord> getBccReleaseManifestByFromAccId(ULong fromAccId, ULong releaseId) {
        return dslContext.selectFrom(BCC_RELEASE_MANIFEST)
                .where(and(BCC_RELEASE_MANIFEST.FROM_ACC_ID.eq(fromAccId),
                        BCC_RELEASE_MANIFEST.RELEASE_ID.eq(releaseId)))
                .fetch();
    }

    public void deleteAccReleaseManifestById(long manifestId) {
        dslContext.deleteFrom(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteAsccpReleaseManifestById(long manifestId) {
        dslContext.deleteFrom(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public void deleteBccpReleaseManifestById(long manifestId) {
        dslContext.deleteFrom(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_RELEASE_MANIFEST_ID.eq(ULong.valueOf(manifestId)))
                .execute();
    }

    public boolean isAccUsed(long accId) {
        return dslContext.selectCount()
                .from(ACC_RELEASE_MANIFEST)
                .where(ACC_RELEASE_MANIFEST.ACC_ID.eq(ULong.valueOf(accId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isAsccpUsed(long asccpId) {
        return dslContext.selectCount()
                .from(ASCCP_RELEASE_MANIFEST)
                .where(ASCCP_RELEASE_MANIFEST.ASCCP_ID.eq(ULong.valueOf(asccpId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isBccpUsed(long bccpId) {
        return dslContext.selectCount()
                .from(BCCP_RELEASE_MANIFEST)
                .where(BCCP_RELEASE_MANIFEST.BCCP_ID.eq(ULong.valueOf(bccpId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isAsccUsed(long asccId) {
        return dslContext.selectCount()
                .from(ASCC_RELEASE_MANIFEST)
                .where(ASCC_RELEASE_MANIFEST.ASCC_ID.eq(ULong.valueOf(asccId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

    public boolean isBccUsed(long bccId) {
        return dslContext.selectCount()
                .from(BCC_RELEASE_MANIFEST)
                .where(BCC_RELEASE_MANIFEST.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOptionalInto(Integer.class).orElse(0) > 0;
    }

}
