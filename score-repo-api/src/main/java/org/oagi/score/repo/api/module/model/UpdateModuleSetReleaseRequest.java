package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

public class UpdateModuleSetReleaseRequest extends Request {

    private String moduleSetReleaseId;

    private String moduleSetId;

    private String releaseId;

    private String moduleSetReleaseName;

    private String moduleSetReleaseDescription;

    private boolean isDefault;

    public String getModuleSetReleaseId() {
        return moduleSetReleaseId;
    }

    public void setModuleSetReleaseId(String moduleSetReleaseId) {
        this.moduleSetReleaseId = moduleSetReleaseId;
    }

    public String getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(String moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getModuleSetReleaseName() {
        return moduleSetReleaseName;
    }

    public void setModuleSetReleaseName(String moduleSetReleaseName) {
        this.moduleSetReleaseName = moduleSetReleaseName;
    }

    public String getModuleSetReleaseDescription() {
        return moduleSetReleaseDescription;
    }

    public void setModuleSetReleaseDescription(String moduleSetReleaseDescription) {
        this.moduleSetReleaseDescription = moduleSetReleaseDescription;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UpdateModuleSetReleaseRequest(ScoreUser requester) {
        super(requester);
    }
}
