package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;

import static org.oagi.srt.entity.jooq.tables.BccpManifest.BCCP_MANIFEST;

public class UpdateBccpManifestArguments {

    private ULong bccpManifestId;
    private ULong bccpId;
    private ULong releaseId;
    private ULong moduleId;
    private ULong bdtManifestId;

    public UpdateBccpManifestArguments(BccpManifestRecord bccpManifestRecord) {
        if (bccpManifestRecord != null) {
            this.bccpManifestId = bccpManifestRecord.getBccpManifestId();
            this.bccpId = bccpManifestRecord.getBccpId();
            this.releaseId = bccpManifestRecord.getReleaseId();
            this.moduleId = bccpManifestRecord.getModuleId();
            this.bdtManifestId = bccpManifestRecord.getBdtManifestId();
        }
    }

    public ULong getBccpManifestId() {
        return bccpManifestId;
    }

    public UpdateBccpManifestArguments setBccpManifestId(ULong bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
        return this;
    }

    public ULong getBccpId() {
        return bccpId;
    }

    public UpdateBccpManifestArguments setBccpId(ULong bccpId) {
        this.bccpId = bccpId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateBccpManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getModuleId() {
        return moduleId;
    }

    public UpdateBccpManifestArguments setModuleId(ULong moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public ULong getBdtManifestId() {
        return bdtManifestId;
    }

    public UpdateBccpManifestArguments setBdtManifestId(ULong bdtManifestId) {
        this.bdtManifestId = bdtManifestId;
        return this;
    }

    public void execute(DSLContext dslContext) {
        updateBccpManifest(dslContext, this);
    }

    private void updateBccpManifest(DSLContext dslContext, UpdateBccpManifestArguments arguments) {
        dslContext.update(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(arguments.getBccpManifestId()))
                .execute();
    }
}
