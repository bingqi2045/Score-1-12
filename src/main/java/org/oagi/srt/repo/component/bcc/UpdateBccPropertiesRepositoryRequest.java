package org.oagi.srt.repo.component.bcc;

import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateBccPropertiesRepositoryRequest extends RepositoryRequest {

    private final BigInteger bccManifestId;

    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private String definition;
    private String definitionSource;
    private BCCEntityType entityType;
    private boolean deprecated;
    private boolean nillable;
    private String defaultValue;
    private String fixedValue;

    public UpdateBccPropertiesRepositoryRequest(User user,
                                                BigInteger bccManifestId) {
        super(user);
        this.bccManifestId = bccManifestId;
    }

    public UpdateBccPropertiesRepositoryRequest(User user,
                                                LocalDateTime localDateTime,
                                                BigInteger bccManifestId) {
        super(user, localDateTime);
        this.bccManifestId = bccManifestId;
    }

    public BigInteger getBccManifestId() {
        return bccManifestId;
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

    public BCCEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(BCCEntityType entityType) {
        this.entityType = entityType;
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
}