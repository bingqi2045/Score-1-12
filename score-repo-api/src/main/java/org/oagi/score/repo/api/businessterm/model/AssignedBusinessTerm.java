package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;
import java.util.Date;

public class AssignedBusinessTerm extends Auditable {

    private BigInteger assignedBizTermId;
    private String bieId;
    private String bieType;
    private Boolean isPrimary;
    private String primaryIndicator;
    private String typeCode;
    private String den;
    private BigInteger businessTermId;
    private String businessTerm;
    private String externalReferenceUri;
    private Date lastUpdateTimestamp;

    private String owner;
    private String lastUpdateUser;

    public BigInteger getAssignedBizTermId() {
        return assignedBizTermId;
    }

    public void setAssignedBizTermId(BigInteger assignedBizTermId) {
        this.assignedBizTermId = assignedBizTermId;
    }

    public String getBieId() {
        return bieId;
    }

    public void setBieId(String bieId) {
        this.bieId = bieId;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public BigInteger getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(BigInteger businessTermId) {
        this.businessTermId = businessTermId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDen() {
        return den;
    }

    public void setDen(String den) {
        this.den = den;
    }

    public String getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(String businessTerm) {
        this.businessTerm = businessTerm;
    }

    public String getExternalReferenceUri() {
        return externalReferenceUri;
    }

    public void setExternalReferenceUri(String externalReferenceUri) {
        this.externalReferenceUri = externalReferenceUri;
    }

    @Override
    public Date getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    public void setPrimaryIndicator(String primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }

    public AssignedBusinessTerm() {
    }

    public AssignedBusinessTerm(BigInteger assignedBizTermId, String bieId, String bieType, Boolean isPrimary, String primaryIndicator, String typeCode, String den, BigInteger businessTermId, String businessTerm, String externalReferenceUri, Date lastUpdateTimestamp, String owner, String lastUpdateUser) {
        this.assignedBizTermId = assignedBizTermId;
        this.bieId = bieId;
        this.bieType = bieType;
        this.isPrimary = isPrimary;
        this.primaryIndicator = primaryIndicator;
        this.typeCode = typeCode;
        this.den = den;
        this.businessTermId = businessTermId;
        this.businessTerm = businessTerm;
        this.externalReferenceUri = externalReferenceUri;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.owner = owner;
        this.lastUpdateUser = lastUpdateUser;
    }
}
