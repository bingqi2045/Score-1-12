package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateAsccpPropertiesRepositoryRequest extends RepositoryRequest {

    private final String asccpManifestId;

    private String propertyTerm;
    private String definition;
    private String definitionSource;
    private boolean reusable;
    private boolean deprecated;
    private boolean nillable;
    private String namespaceId;

    public UpdateAsccpPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                  String asccpManifestId) {
        super(user);
        this.asccpManifestId = asccpManifestId;
    }

    public UpdateAsccpPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                  LocalDateTime localDateTime,
                                                  String asccpManifestId) {
        super(user, localDateTime);
        this.asccpManifestId = asccpManifestId;
    }

    public String getAsccpManifestId() {
        return asccpManifestId;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }
}
