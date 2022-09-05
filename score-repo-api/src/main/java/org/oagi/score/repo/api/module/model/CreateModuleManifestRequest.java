package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.corecomponent.model.CcType;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateModuleManifestRequest extends Request {

    public CreateModuleManifestRequest(ScoreUser requester) {
        super(requester);
    }

    private String manifestId;

    private CcType type;

    private String moduleSetReleaseId;

    private String moduleId;

    private LocalDateTime timestamp;

    public String getManifestId() {
        return manifestId;
    }

    public void setManifestId(String manifestId) {
        this.manifestId = manifestId;
    }

    public CcType getType() {
        return type;
    }

    public void setType(CcType type) {
        this.type = type;
    }

    public String getModuleSetReleaseId() {
        return moduleSetReleaseId;
    }

    public void setModuleSetReleaseId(String moduleSetReleaseId) {
        this.moduleSetReleaseId = moduleSetReleaseId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
