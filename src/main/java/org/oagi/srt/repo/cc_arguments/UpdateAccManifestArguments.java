package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.springframework.stereotype.Repository;

import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;

@Repository
public class UpdateAccManifestArguments {

    private ULong accManifestId;
    private ULong accId;
    private ULong releaseId;
    private ULong moduleId;
    private ULong basedAccManifestId;

    public ULong getAccManifestId() {
        return accManifestId;
    }

    public UpdateAccManifestArguments setAccManifestId(ULong accManifestId) {
        this.accManifestId = accManifestId;
        return this;
    }

    public ULong getAccId() {
        return accId;
    }

    public UpdateAccManifestArguments setAccId(ULong accId) {
        this.accId = accId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateAccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getModuleId() {
        return moduleId;
    }

    public UpdateAccManifestArguments setModuleId(ULong moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public ULong getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public UpdateAccManifestArguments setBasedAccManifestId(ULong basedAccManifestId) {
        this.basedAccManifestId = basedAccManifestId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return updateAccManifest(dslContext,this);
    }

    private ULong updateAccManifest(DSLContext dslContext, UpdateAccManifestArguments arguments) {
        return dslContext.update(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, arguments.getBasedAccManifestId())
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(arguments.getAccManifestId()))
                .returning(ACC_MANIFEST.ACC_MANIFEST_ID).fetchOne().getAccManifestId();
    }
}
