package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.score.repo.CoreComponentRepository;

import java.util.Objects;

public class UpdateAccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong accManifestId;
    private ULong accId;
    private ULong releaseId;
    private ULong basedAccManifestId;

    private int _hashCode;

    public UpdateAccManifestArguments(CoreComponentRepository repository, AccManifestRecord accManifestRecord) {
        this.repository = repository;
        if (accManifestRecord != null) {
            this.accManifestId = accManifestRecord.getAccManifestId();
            this.accId = accManifestRecord.getAccId();
            this.releaseId = accManifestRecord.getReleaseId();
            this.basedAccManifestId = accManifestRecord.getBasedAccManifestId();
        }
        this._hashCode = this.hashCode();
    }

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

    public ULong getBasedAccManifestId() {
        return basedAccManifestId;
    }

    public UpdateAccManifestArguments setBasedAccManifestId(ULong basedAccManifestId) {
        this.basedAccManifestId = basedAccManifestId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAccManifestArguments that = (UpdateAccManifestArguments) o;
        return Objects.equals(accId, that.accId) &&
                Objects.equals(basedAccManifestId, that.basedAccManifestId) &&
                Objects.equals(releaseId, that.releaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accId, basedAccManifestId, releaseId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public void execute() {
        if (!isDirty()) {
            return;
        }

        repository.execute(this);
    }
}
