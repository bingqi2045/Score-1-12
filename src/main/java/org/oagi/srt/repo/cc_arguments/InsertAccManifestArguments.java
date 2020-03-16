package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;

import static org.oagi.srt.entity.jooq.tables.AccManifest.ACC_MANIFEST;

public class InsertAccManifestArguments {

    private ULong accManifestId;
    private ULong accId;
    private ULong releaseId;
    private ULong moduleId;
    private ULong basedAccManifestId;

    public ULong getAccManifestId() {
        return accManifestId;
    }

    public InsertAccManifestArguments setAccManifestId(ULong accManifestId) {
        this.accManifestId = accManifestId;
        return this;
    }

    public ULong getAccId() {
        return accId;
    }

    public InsertAccManifestArguments setAccId(ULong accId) {
        this.accId = accId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public InsertAccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getModuleId() {
        return moduleId;
    }

    public InsertAccManifestArguments setModuleId(ULong moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public ULong getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public InsertAccManifestArguments setBasedAccManifestId(ULong basedAccManifestId) {
        this.basedAccManifestId = basedAccManifestId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return insertAccManifest(dslContext, this);
    }

    private ULong insertAccManifest(DSLContext dslContext, InsertAccManifestArguments arguments) {
        return dslContext.insertInto(ACC_MANIFEST)
                .set(ACC_MANIFEST.ACC_MANIFEST_ID, arguments.getAccManifestId())
                .set(ACC_MANIFEST.ACC_ID, arguments.getAccId())
                .set(ACC_MANIFEST.RELEASE_ID, arguments.getReleaseId())
                .set(ACC_MANIFEST.MODULE_ID, arguments.getModuleId())
                .set(ACC_MANIFEST.BASED_ACC_MANIFEST_ID, arguments.getBasedAccManifestId())
                .returning(ACC_MANIFEST.ACC_MANIFEST_ID).fetchOne().getAccManifestId();
    }
}
