package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Response;

public class CreateModuleDirResponse extends Response {

    private final ModuleDir moduleDir;

    public CreateModuleDirResponse(ModuleDir moduleDir) {
        this.moduleDir = moduleDir;
    }

    public ModuleDir getModuleDir() {
        return moduleDir;
    }
}
