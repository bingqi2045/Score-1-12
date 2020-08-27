package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.CoreComponentRepository;

public class InsertAsccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong asccManifestId;
    private ULong asccId;
    private ULong releaseId;
    private ULong fromAccManifestId;
    private ULong toAsccpManifestId;

    public InsertAsccManifestArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getAsccManifestId() {
        return asccManifestId;
    }

    public InsertAsccManifestArguments setAsccManifestId(ULong asccManifestId) {
        this.asccManifestId = asccManifestId;
        return this;
    }

    public ULong getAsccId() {
        return asccId;
    }

    public InsertAsccManifestArguments setAsccId(ULong asccId) {
        this.asccId = asccId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public InsertAsccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getFromAccManifestId() {
        return fromAccManifestId;
    }

    public InsertAsccManifestArguments setFromAccManifestId(ULong fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
        return this;
    }

    public ULong getToAsccpManifestId() {
        return toAsccpManifestId;
    }

    public InsertAsccManifestArguments setToAsccpManifestId(ULong toAsccpManifestId) {
        this.toAsccpManifestId = toAsccpManifestId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
