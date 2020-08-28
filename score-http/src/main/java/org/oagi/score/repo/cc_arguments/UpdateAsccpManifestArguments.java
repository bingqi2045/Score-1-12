package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccpManifestRecord;
import org.oagi.score.repo.CoreComponentRepository;

import java.util.Objects;

public class UpdateAsccpManifestArguments {

    private final CoreComponentRepository repository;

    private ULong asccpManifestId;
    private ULong asccpId;
    private ULong releaseId;
    private ULong roleOfAccManifestId;

    private int _hashCode;

    public UpdateAsccpManifestArguments(CoreComponentRepository repository, AsccpManifestRecord asccpManifestRecord) {
        this.repository = repository;
        if (asccpManifestRecord != null) {
            this.asccpManifestId = asccpManifestRecord.getAsccpManifestId();
            this.asccpId = asccpManifestRecord.getAsccpId();
            this.releaseId = asccpManifestRecord.getReleaseId();
            this.roleOfAccManifestId = asccpManifestRecord.getRoleOfAccManifestId();
        }
        this._hashCode = this.hashCode();
    }

    public ULong getAsccpManifestId() {
        return asccpManifestId;
    }

    public UpdateAsccpManifestArguments setAsccpManifestId(ULong asccpManifestId) {
        this.asccpManifestId = asccpManifestId;
        return this;
    }

    public ULong getAsccpId() {
        return asccpId;
    }

    public UpdateAsccpManifestArguments setAsccpId(ULong asccpId) {
        this.asccpId = asccpId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateAsccpManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }

    public UpdateAsccpManifestArguments setRoleOfAccManifestId(ULong roleOfAccManifestId) {
        this.roleOfAccManifestId = roleOfAccManifestId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAsccpManifestArguments that = (UpdateAsccpManifestArguments) o;
        return Objects.equals(asccpId, that.asccpId) &&
                Objects.equals(roleOfAccManifestId, that.roleOfAccManifestId) &&
                Objects.equals(releaseId, that.releaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asccpId, roleOfAccManifestId, releaseId);
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
