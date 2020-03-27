package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.repo.CoreComponentRepository;

public class InsertBccpManifestArguments {

    private final CoreComponentRepository repository;

    private ULong bccpManifestId;
    private ULong bccpId;
    private ULong releaseId;
    private ULong moduleId;
    private ULong bdtManifestId;

    public InsertBccpManifestArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

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

    public ULong execute() {
        return repository.execute(this);
    }
}
