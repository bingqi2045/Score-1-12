package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;
import org.oagi.score.repo.api.module.model.AssignableNode;

import java.util.HashMap;
import java.util.Map;

@Data
public class ModuleAssignComponents {

    private Map<String, AssignableNode> assignableAccManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableAsccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableBccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableDtManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableCodeListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableAgencyIdListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableXbtManifestMap = new HashMap();

    private Map<String, AssignableNode> assignedAccManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedAsccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedBccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedDtManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedCodeListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedAgencyIdListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignedXbtManifestMap = new HashMap();

    public Map<String, AssignableNode> getAssignableAccManifestMap() {
        return assignableAccManifestMap;
    }

    public void setAssignableAccManifestMap(Map<String, AssignableNode> assignableAccManifestMap) {
        this.assignableAccManifestMap = assignableAccManifestMap;
    }

    public Map<String, AssignableNode> getAssignableAsccpManifestMap() {
        return assignableAsccpManifestMap;
    }

    public void setAssignableAsccpManifestMap(Map<String, AssignableNode> assignableAsccpManifestMap) {
        this.assignableAsccpManifestMap = assignableAsccpManifestMap;
    }

    public Map<String, AssignableNode> getAssignableBccpManifestMap() {
        return assignableBccpManifestMap;
    }

    public void setAssignableBccpManifestMap(Map<String, AssignableNode> assignableBccpManifestMap) {
        this.assignableBccpManifestMap = assignableBccpManifestMap;
    }

    public Map<String, AssignableNode> getAssignableDtManifestMap() {
        return assignableDtManifestMap;
    }

    public void setAssignableDtManifestMap(Map<String, AssignableNode> assignableDtManifestMap) {
        this.assignableDtManifestMap = assignableDtManifestMap;
    }

    public Map<String, AssignableNode> getAssignableCodeListManifestMap() {
        return assignableCodeListManifestMap;
    }

    public void setAssignableCodeListManifestMap(Map<String, AssignableNode> assignableCodeListManifestMap) {
        this.assignableCodeListManifestMap = assignableCodeListManifestMap;
    }

    public Map<String, AssignableNode> getAssignableAgencyIdListManifestMap() {
        return assignableAgencyIdListManifestMap;
    }

    public void setAssignableAgencyIdListManifestMap(Map<String, AssignableNode> assignableAgencyIdListManifestMap) {
        this.assignableAgencyIdListManifestMap = assignableAgencyIdListManifestMap;
    }

    public Map<String, AssignableNode> getAssignableXbtManifestMap() {
        return assignableXbtManifestMap;
    }

    public void setAssignableXbtManifestMap(Map<String, AssignableNode> assignableXbtManifestMap) {
        this.assignableXbtManifestMap = assignableXbtManifestMap;
    }

    public Map<String, AssignableNode> getAssignedAccManifestMap() {
        return assignedAccManifestMap;
    }

    public void setAssignedAccManifestMap(Map<String, AssignableNode> assignedAccManifestMap) {
        this.assignedAccManifestMap = assignedAccManifestMap;
    }

    public Map<String, AssignableNode> getAssignedAsccpManifestMap() {
        return assignedAsccpManifestMap;
    }

    public void setAssignedAsccpManifestMap(Map<String, AssignableNode> assignedAsccpManifestMap) {
        this.assignedAsccpManifestMap = assignedAsccpManifestMap;
    }

    public Map<String, AssignableNode> getAssignedBccpManifestMap() {
        return assignedBccpManifestMap;
    }

    public void setAssignedBccpManifestMap(Map<String, AssignableNode> assignedBccpManifestMap) {
        this.assignedBccpManifestMap = assignedBccpManifestMap;
    }

    public Map<String, AssignableNode> getAssignedDtManifestMap() {
        return assignedDtManifestMap;
    }

    public void setAssignedDtManifestMap(Map<String, AssignableNode> assignedDtManifestMap) {
        this.assignedDtManifestMap = assignedDtManifestMap;
    }

    public Map<String, AssignableNode> getAssignedCodeListManifestMap() {
        return assignedCodeListManifestMap;
    }

    public void setAssignedCodeListManifestMap(Map<String, AssignableNode> assignedCodeListManifestMap) {
        this.assignedCodeListManifestMap = assignedCodeListManifestMap;
    }

    public Map<String, AssignableNode> getAssignedAgencyIdListManifestMap() {
        return assignedAgencyIdListManifestMap;
    }

    public void setAssignedAgencyIdListManifestMap(Map<String, AssignableNode> assignedAgencyIdListManifestMap) {
        this.assignedAgencyIdListManifestMap = assignedAgencyIdListManifestMap;
    }

    public Map<String, AssignableNode> getAssignedXbtManifestMap() {
        return assignedXbtManifestMap;
    }

    public void setAssignedXbtManifestMap(Map<String, AssignableNode> assignedXbtManifestMap) {
        this.assignedXbtManifestMap = assignedXbtManifestMap;
    }
}
