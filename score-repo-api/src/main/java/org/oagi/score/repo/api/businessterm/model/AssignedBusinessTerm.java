package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Date;

public class AssignedBusinessTerm extends Auditable {

    private BigInteger assignedBtId;

    private boolean isPrimary;

    private BigInteger bieId;

    private String biePropertyTerm;

    private String bieType;

    private BigInteger businessTermId;

    private String businessTerm;

    private String definition;

    private String externalReferenceUri;

    private String externalReferenceId;

    private String guid;

    private Date lastUpdateTimestamp;

    private ScoreUser createdBy;
    private ScoreUser lastUpdatedBy;

    public BigInteger getAssignedBtId() {
        return assignedBtId;
    }

    public void setAssignedBtId(BigInteger assignedBtId) {
        this.assignedBtId = assignedBtId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public BigInteger getBieId() {
        return bieId;
    }

    public void setBieId(BigInteger bieId) {
        this.bieId = bieId;
    }

    public String getBiePropertyTerm() {
        return biePropertyTerm;
    }

    public void setBiePropertyTerm(String biePropertyTerm) {
        this.biePropertyTerm = biePropertyTerm;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public BigInteger getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(BigInteger businessTermId) {
        this.businessTermId = businessTermId;
    }

    public String getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(String businessTerm) {
        this.businessTerm = businessTerm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExternalReferenceUri() {
        return externalReferenceUri;
    }

    public void setExternalReferenceUri(String externalReferenceUri) {
        this.externalReferenceUri = externalReferenceUri;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public void setCreatedBy(ScoreUser createdBy) {
        this.createdBy = createdBy;
    }

    public ScoreUser getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(ScoreUser lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public ScoreUser getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    @Override
    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public AssignedBusinessTerm(BigInteger assignedBtId, boolean isPrimary, BigInteger bieId, String biePropertyTerm, String bieType, BigInteger businessTermId, String businessTerm, String definition, String externalReferenceUri, String externalReferenceId, String guid, Date lastUpdateTimestamp, ScoreUser createdBy, ScoreUser lastUpdatedBy) {
        this.assignedBtId = assignedBtId;
        this.isPrimary = isPrimary;
        this.bieId = bieId;
        this.biePropertyTerm = biePropertyTerm;
        this.bieType = bieType;
        this.businessTermId = businessTermId;
        this.businessTerm = businessTerm;
        this.definition = definition;
        this.externalReferenceUri = externalReferenceUri;
        this.externalReferenceId = externalReferenceId;
        this.guid = guid;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
