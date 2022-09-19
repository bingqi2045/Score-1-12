package org.oagi.score.repo.component.ascc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpdateAsccPropertiesRepositoryRequest extends RepositoryRequest {

    private final String asccManifestId;

    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private String definition;
    private String definitionSource;
    private boolean deprecated;

    public UpdateAsccPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                 String asccManifestId) {
        super(user);
        this.asccManifestId = asccManifestId;
    }

    public UpdateAsccPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                 LocalDateTime localDateTime,
                                                 String asccManifestId) {
        super(user, localDateTime);
        this.asccManifestId = asccManifestId;
    }

    public String getAsccManifestId() {
        return asccManifestId;
    }

    public Integer getCardinalityMin() {
        return cardinalityMin;
    }

    public void setCardinalityMin(Integer cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
    }

    public Integer getCardinalityMax() {
        return cardinalityMax;
    }

    public void setCardinalityMax(Integer cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
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

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
