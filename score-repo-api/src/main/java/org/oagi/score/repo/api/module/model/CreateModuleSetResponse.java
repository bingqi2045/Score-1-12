package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Response;

public class CreateModuleSetResponse extends Response {

    private final ModuleSet moduleSet;

    private String rootModuleId;

    public String getRootModuleId() {
        return rootModuleId;
    }

    public void setRootModuleId(String rootModuleId) {
        this.rootModuleId = rootModuleId;
    }

    public CreateModuleSetResponse(ModuleSet moduleSet) {
        this.moduleSet = moduleSet;
    }

    public ModuleSet getModuleSet() {
        return moduleSet;
    }
}
