package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.repo.CoreComponentRepository;

public class InsertBccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong bccManifestId;
    private ULong bccId;
    private ULong releaseId;
    private ULong fromAccManifestId;
    private ULong toBccManifestId;

    public InsertBccManifestArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getBccManifestId() {
        return bccManifestId;
    }

    public InsertBccManifestArguments setBccManifestId(ULong bccManifestId) {
        this.bccManifestId = bccManifestId;
        return this;
    }

    public ULong getBccId() {
        return bccId;
    }

    public InsertBccManifestArguments setBccId(ULong bccId) {
        this.bccId = bccId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public InsertBccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getFromAccManifestId() {
        return fromAccManifestId;
    }

    public InsertBccManifestArguments setFromAccManifestId(ULong fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
        return this;
    }

    public ULong getToBccManifestId() {
        return toBccManifestId;
    }

    public InsertBccManifestArguments setToBccpManifestId(ULong toBccManifestId) {
        this.toBccManifestId = toBccManifestId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
