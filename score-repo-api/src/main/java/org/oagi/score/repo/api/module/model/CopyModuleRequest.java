package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

public class CopyModuleRequest extends Request {

    private String moduleSetId;

    private String targetModuleId;

    public boolean isCopySubModules() {
        return copySubModules;
    }

    public void setCopySubModules(boolean copySubModules) {
        this.copySubModules = copySubModules;
    }

    private boolean copySubModules;

    public String getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(String moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    public String getTargetModuleId() {
        return targetModuleId;
    }

    public void setTargetModuleId(String targetModuleId) {
        this.targetModuleId = targetModuleId;
    }

    public String getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(String parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    private String parentModuleId;

    public CopyModuleRequest() {
        super();
    }

    @Override
    public ScoreUser getRequester() {
        return requester;
    }

    @Override
    public void setRequester(ScoreUser requester) {
        this.requester = requester;
    }

    private ScoreUser requester;
}
