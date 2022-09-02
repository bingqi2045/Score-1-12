package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;
import java.math.BigInteger;

public class Asccp extends Auditable implements CoreComponent, Serializable {

    private String asccpId;

    private String guid;

    private String type;

    private String propertyTerm;

    private String den;

    private String definition;

    private String definitionSource;

    private String namespaceId;

    private ScoreUser owner;

    private CcState state;

    private boolean deprecated;

    private boolean reusable;

    private boolean nillable;

    private String prevAsccpId;

    private String nextAsccpId;

    public String getAsccpId() {
        return asccpId;
    }

    public void setAsccpId(String asccpId) {
        this.asccpId = asccpId;
    }

    @Override
    public String getId() {
        return getAsccpId();
    }

    @Override
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public String getDen() {
        return den;
    }

    public void setDen(String den) {
        this.den = den;
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

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public ScoreUser getOwner() {
        return owner;
    }

    public void setOwner(ScoreUser owner) {
        this.owner = owner;
    }

    public CcState getState() {
        return state;
    }

    public void setState(CcState state) {
        this.state = state;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public String getPrevAsccpId() {
        return prevAsccpId;
    }

    public void setPrevAsccpId(String prevAsccpId) {
        this.prevAsccpId = prevAsccpId;
    }

    public String getNextAsccpId() {
        return nextAsccpId;
    }

    public void setNextAsccpId(String nextAsccpId) {
        this.nextAsccpId = nextAsccpId;
    }
}
