package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.springframework.stereotype.Repository;

import static org.oagi.srt.entity.jooq.Tables.BCCP_MANIFEST;

@Repository
public class InsertBccpManifestArguments {

    private ULong bccpManifestId;
    private ULong bccpId;
    private ULong releaseId;
    private ULong moduleId;
    private ULong bdtManifestId;

    public ULong getBccpManifestId() {
        return bccpManifestId;
    }

    public InsertBccpManifestArguments setBccpManifestId(ULong bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
        return this;
    }

    public ULong getBccpId() {
        return bccpId;
    }

    public InsertBccpManifestArguments setBccpId(ULong bccpId) {
        this.bccpId = bccpId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public InsertBccpManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getModuleId() {
        return moduleId;
    }

    public InsertBccpManifestArguments setModuleId(ULong moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public ULong getBdtManifestId() {
        return bdtManifestId;
    }

    public InsertBccpManifestArguments setBdtManifestId(ULong bdtManifestId) {
        this.bdtManifestId = bdtManifestId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return insertBccpManifest(dslContext, this);
    }

    private ULong insertBccpManifest(DSLContext dslContext, InsertBccpManifestArguments arguments) {
        return dslContext.insertInto(BCCP_MANIFEST)
                .set(BCCP_MANIFEST.BCCP_MANIFEST_ID, arguments.getBccpManifestId())
                .set(BCCP_MANIFEST.BCCP_ID, arguments.getBccpId())
                .set(BCCP_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(BCCP_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(BCCP_MANIFEST.BDT_MANIFEST_ID, arguments.getBdtManifestId())
                .returning(BCCP_MANIFEST.BCCP_MANIFEST_ID).fetchOne().getBccpManifestId();
    }
}
