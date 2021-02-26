package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Response;

public class UpdateModuleDirResponse extends Response {

    private final ModuleDir moduleDir;

    public UpdateModuleDirResponse(ModuleDir moduleDir) {
        this.moduleDir = moduleDir;
    }

    public ModuleDir getModuleDir() {
        return moduleDir;
    }
}
