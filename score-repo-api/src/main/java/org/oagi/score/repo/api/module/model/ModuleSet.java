package org.oagi.score.repo.api.module.model;

import org.oagi.score.repo.api.base.Auditable;

import java.io.Serializable;

public class ModuleSet extends Auditable implements Comparable<ModuleSet>, Serializable {

    private String moduleSetId;

    private String guid;

    private String name;

    private String description;

    public String getModuleSetId() {
        return moduleSetId;
    }

    public void setModuleSetId(String moduleSetId) {
        this.moduleSetId = moduleSetId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(ModuleSet o) {
        return this.moduleSetId.compareTo(o.getModuleSetId());
    }
}
