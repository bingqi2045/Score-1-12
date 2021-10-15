package org.oagi.score.repo.component.dt_sc;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateDtScPropertiesRepositoryRequest extends RepositoryRequest {

    private final BigInteger dtScManifestId;

    private String propertyTerm;
    private String defaultValue;
    private String fixedValue;
    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private String definition;
    private String definitionSource;
    private boolean deprecated;

    public UpdateDtScPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                 BigInteger dtScManifestId) {
        super(user);
        this.dtScManifestId = dtScManifestId;
    }

    public UpdateDtScPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                 LocalDateTime localDateTime,
                                                 BigInteger dtScManifestId) {
        super(user, localDateTime);
        this.dtScManifestId = dtScManifestId;
    }

    public BigInteger getDtScManifestId() {
        return dtScManifestId;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
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
}
