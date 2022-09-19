package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Response;

public class CreateModuleResponse extends Response {

    private final Module module;

    public CreateModuleResponse(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}
