package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.CoreComponentRepository;

public class InsertAccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong accManifestId;
    private ULong accId;
    private ULong releaseId;
    private ULong basedAccManifestId;

    public InsertAccManifestArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

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

    public ULong getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public InsertAccManifestArguments setBasedAccManifestId(ULong basedAccManifestId) {
        this.basedAccManifestId = basedAccManifestId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
