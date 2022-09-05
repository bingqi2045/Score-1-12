package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;

public class CreateModuleSetReleaseRequest extends Request {

    private String moduleSetId;

    private String releaseId;

    private String moduleSetReleaseName;

    private String moduleSetReleaseDescription;

    private boolean isDefault;

    private String baseModuleSetReleaseId;

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

    public CreateModuleSetReleaseRequest() {
        super();
    }

    public String getBaseModuleSetReleaseId() {
        return baseModuleSetReleaseId;
    }

    public void setBaseModuleSetReleaseId(String baseModuleSetReleaseId) {
        this.baseModuleSetReleaseId = baseModuleSetReleaseId;
    }
}
