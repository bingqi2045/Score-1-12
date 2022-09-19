package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.gateway.http.api.cc_management.data.CcASCCPType;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.service.common.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.oagi.score.gateway.http.api.cc_management.data.CcASCCPType.Verb;

public class CreateAsccpRepositoryRequest extends RepositoryRequest {

    private final String roleOfAccManifestId;
    private final String releaseId;

    private String initialPropertyTerm;
    private CcASCCPType initialType = CcASCCPType.Default;
    private String namespaceId;
    private boolean reusable = true;
    private String definition;
    private String definitionSource;
    private CcState initialState = CcState.WIP;

    private List<String> tags = Collections.emptyList();

    public CreateAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        String roleOfAccManifestId, String releaseId) {
        super(user);
        this.roleOfAccManifestId = roleOfAccManifestId;
        this.releaseId = releaseId;
    }

    public CreateAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        LocalDateTime localDateTime,
                                        String roleOfAccManifestId, String releaseId) {
        super(user, localDateTime);
        this.roleOfAccManifestId = roleOfAccManifestId;
        this.releaseId = releaseId;
    }

    public String getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public String getInitialPropertyTerm() {
        if (!StringUtils.hasLength(initialPropertyTerm)) {
            if (Verb == this.getInitialType()) {
                return "Do";
            } else {
                return "Property Term";
            }
        }
        return initialPropertyTerm;
    }

    public void setInitialPropertyTerm(String initialPropertyTerm) {
        this.initialPropertyTerm = initialPropertyTerm;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public CcState getInitialState() {
        return initialState;
    }

    public void setInitialState(CcState initialState) {
        this.initialState = initialState;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
    }

    public CcASCCPType getInitialType() {
        return initialType;
    }

    public void setInitialType(CcASCCPType initialType) {
        this.initialType = initialType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
