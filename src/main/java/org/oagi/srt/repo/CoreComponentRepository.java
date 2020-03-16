package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.repo.cc_arguments.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.oagi.srt.entity.jooq.Tables.*;
import static org.oagi.srt.entity.jooq.tables.Acc.ACC;

@Repository
public class CoreComponentRepository {

    @Autowired
    private DSLContext dslContext;

    public AccManifestRecord getAccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public AsccpManifestRecord getAsccpManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public ULong getRoleOfAccManifestIdFromAsccpManifestByManifestId(long manifestId) {
        if (manifestId <= 0L) {
            return null;
        }
        return getRoleOfAccManifestIdFromAsccpManifestByManifestId(ULong.valueOf(manifestId));
    }

    public ULong getRoleOfAccManifestIdFromAsccpManifestByManifestId(ULong manifestId) {
        return dslContext.select(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID)
                .from(ASCCP_MANIFEST)
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptionalInto(ULong.class).orElse(null);
    }

    public BccpManifestRecord getBccpManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCCP_MANIFEST)
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public AsccManifestRecord getAsccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(ASCC_MANIFEST)
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public BccManifestRecord getBccManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public DtManifestRecord getBdtManifestByManifestId(ULong manifestId) {
        if (manifestId == null || manifestId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(DT_MANIFEST)
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(manifestId))
                .fetchOptional().orElse(null);
    }

    public BccpRecord getBccpById(ULong bccpId) {
        if (bccpId == null || bccpId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpId))
                .fetchOptional().orElse(null);
    }

    public DtRecord getBdtById(ULong bdtId) {
        if (bdtId == null || bdtId.longValue() <= 0L) {
            return null;
        }
        return dslContext.selectFrom(DT)
                .where(DT.DT_ID.eq(bdtId))
                .fetchOptional().orElse(null);
    }

    public InsertAccArguments insertAccArguments() {
        return new InsertAccArguments();
    }

    public InsertBccpArguments insertBccpArguments() {
        return new InsertBccpArguments();
    }

    public InsertAccManifestArguments insertAccManifestArguments() {
        return new InsertAccManifestArguments();
    }

    public InsertBccpManifestArguments insertBccpManifestArguments() {
        return new InsertBccpManifestArguments();
    }

    public UpdateAccArguments updateAccArguments() {
        return new UpdateAccArguments();
    }

    public UpdateBccpArguments updateBccpArguments(BccpRecord bccp) {
        return new UpdateBccpArguments(bccp);
    }

    public UpdateAccManifestArguments updateAccManifestArguments() {
        return new UpdateAccManifestArguments();
    }

    public UpdateBccpManifestArguments updateBccpManifestArguments(BccpManifestRecord bccpManifestRecord) {
        return new UpdateBccpManifestArguments(bccpManifestRecord);
    }

    public ULong execute(InsertAccArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(InsertAccManifestArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(InsertBccpArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(InsertBccpManifestArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(UpdateAccArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(UpdateAccManifestArguments arguments) {
        return arguments.execute(dslContext);
    }

    public ULong execute(UpdateBccpArguments arguments) {
        return arguments.execute(dslContext);
    }

    public void execute(UpdateBccpManifestArguments arguments) {
        arguments.execute(dslContext);
    }
}
