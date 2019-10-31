package org.oagi.srt.gateway.http.api.module_management.data.module_edit;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleElementDependency implements Serializable {
    private String dependencyType;
    private ModuleElement dependingElement;
    private ModuleElement dependedElement;

    public ModuleElementDependency(String dependencyType, ModuleElement dependingElement) {
        this.dependencyType = dependencyType;
        this.dependingElement = dependingElement;
    }

    public String toString(int indent) {
        return toString(indent, getDependedElement());
    }

    public String toString(int indent, ModuleElement from) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            sb.append("\t");
        }
        sb.append("[").append(getDependencyType())
                .append(": ")
                .append(getDependingElement().getRelativePath(from))
                .append("]");
        return sb.toString();
    }
}
