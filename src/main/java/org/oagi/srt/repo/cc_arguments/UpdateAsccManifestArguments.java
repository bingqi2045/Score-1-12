package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsccManifestRecord;
import org.oagi.srt.repo.CoreComponentRepository;

import java.util.Objects;

public class UpdateAsccManifestArguments {

    private final CoreComponentRepository repository;

    private ULong asccManifestId;
    private ULong asccId;
    private ULong releaseId;
    private ULong fromAccManifestId;
    private ULong toAsccpManifestId;

    private int _hashCode;

    public UpdateAsccManifestArguments(CoreComponentRepository repository, AsccManifestRecord asccManifestRecord) {
        this.repository = repository;
        if (asccManifestRecord != null) {
            this.asccManifestId = asccManifestRecord.getAsccManifestId();
            this.asccId = asccManifestRecord.getAsccId();
            this.releaseId = asccManifestRecord.getReleaseId();
            this.fromAccManifestId = asccManifestRecord.getFromAccManifestId();
            this.toAsccpManifestId = asccManifestRecord.getToAsccpManifestId();
        }
        this._hashCode = this.hashCode();
    }

    public ULong getAsccManifestId() {
        return asccManifestId;
    }

    public UpdateAsccManifestArguments setAsccManifestId(ULong asccManifestId) {
        this.asccManifestId = asccManifestId;
        return this;
    }

    public ULong getAsccId() {
        return asccId;
    }

    public UpdateAsccManifestArguments setAsccId(ULong asccId) {
        this.asccId = asccId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateAsccManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getFromAccManifestId() {
        return fromAccManifestId;
    }

    public UpdateAsccManifestArguments setFromAccManifestId(ULong fromAccManifestId) {
        this.fromAccManifestId = fromAccManifestId;
        return this;
    }

    public ULong getToAsccpManifestId() {
        return toAsccpManifestId;
    }

    public UpdateAsccManifestArguments setToAsccpManifestId(ULong toAsccpManifestId) {
        this.toAsccpManifestId = toAsccpManifestId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAsccManifestArguments that = (UpdateAsccManifestArguments) o;
        return Objects.equals(asccId, that.asccId) &&
                Objects.equals(fromAccManifestId, that.fromAccManifestId) &&
                Objects.equals(toAsccpManifestId, that.toAsccpManifestId) &&
                Objects.equals(releaseId, that.releaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asccId, fromAccManifestId, toAsccpManifestId, releaseId);
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
