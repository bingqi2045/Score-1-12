package org.oagi.score.repo.api.corecomponent.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;
import java.math.BigInteger;

public class Dt extends Auditable implements CoreComponent, Serializable {

    private String dtId;

    private String guid;

    private String dataTypeTerm;

    private String qualifier;

    private String representationTerm;

    private String sixDigitId;

    private String den;

    private String definition;

    private String definitionSource;

    private String contentComponentDefinition;

    private String namespaceId;

    private ScoreUser owner;

    private CcState state;

    private boolean deprecated;

    private boolean commonlyUsed;

    private String prevDtId;

    private String nextDtId;

    public String getDtId() {
        return dtId;
    }

    public void setDtId(String dtId) {
        this.dtId = dtId;
    }

    @Override
    public String getId() {
        return getDtId();
    }

    @Override
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDataTypeTerm() {
        return dataTypeTerm;
    }

    public void setDataTypeTerm(String dataTypeTerm) {
        this.dataTypeTerm = dataTypeTerm;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
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

    public String getRepresentationTerm() {
        return representationTerm;
    }

    public void setRepresentationTerm(String representationTerm) {
        this.representationTerm = representationTerm;
    }

    public String getSixDigitId() {
        return sixDigitId;
    }

    public void setSixDigitId(String sixDigitId) {
        this.sixDigitId = sixDigitId;
    }

    public String getContentComponentDefinition() {
        return contentComponentDefinition;
    }

    public void setContentComponentDefinition(String contentComponentDefinition) {
        this.contentComponentDefinition = contentComponentDefinition;
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

    public boolean isCommonlyUsed() {
        return commonlyUsed;
    }

    public void setCommonlyUsed(boolean commonlyUsed) {
        this.commonlyUsed = commonlyUsed;
    }

    public String getPrevDtId() {
        return prevDtId;
    }

    public void setPrevDtId(String prevDtId) {
        this.prevDtId = prevDtId;
    }

    public String getNextDtId() {
        return nextDtId;
    }

    public void setNextDtId(String nextDtId) {
        this.nextDtId = nextDtId;
    }
}
