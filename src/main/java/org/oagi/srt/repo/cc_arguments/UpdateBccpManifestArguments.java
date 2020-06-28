package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.oagi.srt.repo.CoreComponentRepository;

import java.util.Objects;

public class UpdateBccpManifestArguments {

    private final CoreComponentRepository repository;

    private ULong bccpManifestId;
    private ULong bccpId;
    private ULong releaseId;
    private ULong bdtManifestId;

    private int _hashCode;

    public UpdateBccpManifestArguments(CoreComponentRepository repository, BccpManifestRecord bccpManifestRecord) {
        this.repository = repository;
        if (bccpManifestRecord != null) {
            this.bccpManifestId = bccpManifestRecord.getBccpManifestId();
            this.bccpId = bccpManifestRecord.getBccpId();
            this.releaseId = bccpManifestRecord.getReleaseId();
            this.bdtManifestId = bccpManifestRecord.getBdtManifestId();
        }
        this._hashCode = this.hashCode();
    }

    public ULong getBccpManifestId() {
        return bccpManifestId;
    }

    public UpdateBccpManifestArguments setBccpManifestId(ULong bccpManifestId) {
        this.bccpManifestId = bccpManifestId;
        return this;
    }

    public ULong getBccpId() {
        return bccpId;
    }

    public UpdateBccpManifestArguments setBccpId(ULong bccpId) {
        this.bccpId = bccpId;
        return this;
    }

    public ULong getReleaseId() {
        return releaseId;
    }

    public UpdateBccpManifestArguments setReleaseId(ULong releaseId) {
        this.releaseId = releaseId;
        return this;
    }

    public ULong getBdtManifestId() {
        return bdtManifestId;
    }

    public UpdateBccpManifestArguments setBdtManifestId(ULong bdtManifestId) {
        this.bdtManifestId = bdtManifestId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBccpManifestArguments that = (UpdateBccpManifestArguments) o;
        return Objects.equals(bccpId, that.bccpId) &&
                Objects.equals(bdtManifestId, that.bdtManifestId) &&
                Objects.equals(releaseId, that.releaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bccpId, bdtManifestId, releaseId);
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
