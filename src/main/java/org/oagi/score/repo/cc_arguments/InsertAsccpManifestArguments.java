package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.CoreComponentRepository;

public class InsertAsccpManifestArguments {

    private final CoreComponentRepository repository;

    private ULong asccpManifestId;
    private ULong asccpId;
    private ULong releaseId;
    private ULong roleOfAccManifestId;

    public InsertAsccpManifestArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getAsccpManifestId() {
        return asccpManifestId;
    }

    public InsertAsccpManifestArguments setAsccpManifestId(ULong asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
        return this;
    }

    public ULong getAsccpId() {
        return asccpId;
    }

    public InsertAsccpManifestArguments setAsccpId(ULong asccpId) {
        this.asccpId = asccpId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public InsertAsccpManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }

    public InsertAsccpManifestArguments setRoleOfAccManifestId(ULong roleOfAccManifestId) {
        this.roleOfAccManifestId = roleOfAccManifestId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
