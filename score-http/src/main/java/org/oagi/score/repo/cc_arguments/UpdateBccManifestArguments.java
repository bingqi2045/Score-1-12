package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccManifestRecord;
import org.oagi.score.repo.CoreComponentRepository;

import java.util.Objects;

public class UpdateBccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong bccManifestId;
    private ULong bccId;
    private ULong releaseId;
    private ULong fromAccManifestId;
    private ULong toBccpManifestId;

    private int _hashCode;

    public UpdateBccManifestArguments(CoreComponentRepository repository, BccManifestRecord bccManifestRecord) {
        this.repository = repository;
        if (bccManifestRecord != null) {
            this.bccManifestId = bccManifestRecord.getBccManifestId();
            this.bccId = bccManifestRecord.getBccId();
            this.releaseId = bccManifestRecord.getReleaseId();
            this.fromAccManifestId = bccManifestRecord.getFromAccManifestId();
            this.toBccpManifestId = bccManifestRecord.getToBccpManifestId();
        }
        this._hashCode = this.hashCode();
    }

    public ULong getBccManifestId() {
        return bccManifestId;
    }

    public UpdateBccManifestArguments setBccManifestId(ULong bccManifestId) {
        this.bccManifestId = bccManifestId;
        return this;
    }

    public ULong getBccId() {
        return bccId;
    }

    public UpdateBccManifestArguments setBccId(ULong bccId) {
        this.bccId = bccId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateBccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getFromAccManifestId() {
        return fromAccManifestId;
    }

    public UpdateBccManifestArguments setFromAccManifestId(ULong fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
        return this;
    }

    public ULong getToBccpManifestId() {
        return toBccpManifestId;
    }

    public UpdateBccManifestArguments setToBccpManifestId(ULong toBccpManifestId) {
        this.toBccpManifestId = toBccpManifestId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBccManifestArguments that = (UpdateBccManifestArguments) o;
        return Objects.equals(bccId, that.bccId) &&
                Objects.equals(fromAccManifestId, that.fromAccManifestId) &&
                Objects.equals(toBccpManifestId, that.toBccpManifestId) &&
                Objects.equals(releaseId, that.releaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bccId, fromAccManifestId, toBccpManifestId, releaseId);
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
