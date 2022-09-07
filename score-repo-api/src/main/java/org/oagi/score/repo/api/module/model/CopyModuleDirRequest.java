package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;

import java.math.BigInteger;

public class CopyModuleDirRequest extends Request {

    public String getModuleDirId() {
        return moduleDirId;
    }

    public void setModuleDirId(String moduleDirId) {
        this.moduleDirId = moduleDirId;
    }

    private String moduleDirId;

    public Boolean getCopySubModules() {
        return copySubModules;
    }

    public void setCopySubModules(Boolean copySubModules) {
        this.copySubModules = copySubModules;
    }

    private Boolean copySubModules;

    public String getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(String moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    private String moduleSetId;

    private String copyPosDirId;

    public String getCopyPosDirId() {
        return copyPosDirId;
    }

    public void setCopyPosDirId(String copyPosDirId) {
        this.copyPosDirId = copyPosDirId;
    }
}
