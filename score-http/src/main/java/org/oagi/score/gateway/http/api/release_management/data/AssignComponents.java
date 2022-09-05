package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Data
public class AssignComponents {

    private Map<String, AssignableNode> assignableAccManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableAsccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableBccpManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableCodeListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableAgencyIdListManifestMap = new HashMap();
    private Map<String, AssignableNode> assignableDtManifestMap = new HashMap();

    private Map<String, AssignableNode> unassignableAccManifestMap = new HashMap();
    private Map<String, AssignableNode> unassignableAsccpManifestMap = new HashMap();
    private Map<String, AssignableNode> unassignableBccpManifestMap = new HashMap();
    private Map<String, AssignableNode> unassignableCodeListManifestMap = new HashMap();
    private Map<String, AssignableNode> unassignableAgencyIdListManifestMap = new HashMap();
    private Map<String, AssignableNode> unassignableDtManifestMap = new HashMap();

    public void addAssignableAccManifest(String accManifestId, AssignableNode node) {
        assignableAccManifestMap.put(accManifestId, node);
    }

    public void addAssignableAsccpManifest(String asccpManifestId, AssignableNode node) {
        assignableAsccpManifestMap.put(asccpManifestId, node);
    }

    public void addAssignableBccpManifest(String bccpManifestId, AssignableNode node) {
        assignableBccpManifestMap.put(bccpManifestId, node);
    }

    public void addAssignableCodeListManifest(String codeListManifestId, AssignableNode node) {
        assignableCodeListManifestMap.put(codeListManifestId, node);
    }

    public void addAssignableAgencyIdListManifest(String agencyIdListManifestId, AssignableNode node) {
        assignableAgencyIdListManifestMap.put(agencyIdListManifestId, node);
    }

    public void addAssignableDtManifest(String dtManifestId, AssignableNode node) {
        assignableDtManifestMap.put(dtManifestId, node);
    }

    public void addUnassignableAccManifest(String accManifestId, AssignableNode node) {
        unassignableAccManifestMap.put(accManifestId, node);
    }

    public void addUnassignableAsccpManifest(String asccpManifestId, AssignableNode node) {
        unassignableAsccpManifestMap.put(asccpManifestId, node);
    }

    public void addUnassignableBccpManifest(String bccpManifestId, AssignableNode node) {
        unassignableBccpManifestMap.put(bccpManifestId, node);
    }

    public void addUnassignableCodeListManifest(String codeListManifestId, AssignableNode node) {
        unassignableCodeListManifestMap.put(codeListManifestId, node);
    }

    public void addUnassignableAgencyIdListManifest(String agencyIdListManifestId, AssignableNode node) {
        unassignableAgencyIdListManifestMap.put(agencyIdListManifestId, node);
    }

    public void addUnassignableDtManifest(String dtManifestId, AssignableNode node) {
        unassignableAgencyIdListManifestMap.put(dtManifestId, node);
    }
}
