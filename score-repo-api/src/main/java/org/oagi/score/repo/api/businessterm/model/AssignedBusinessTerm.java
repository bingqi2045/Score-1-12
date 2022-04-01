package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;

import java.math.BigInteger;
import java.util.Date;

public class AssignedBusinessTerm extends Auditable {

    private BigInteger assignedBtId;
    private String bieType;
    private String isPrimary;
    private String typeCode;
    private String den;
    private String businessTerm;
    private String externalReferenceUri;
    private Date lastUpdateTimestamp;

    private String owner;
    private String lastUpdateUser;

    public BigInteger getAssignedBtId() {
        return assignedBtId;
    }

    public void setAssignedBtId(BigInteger assignedBtId) {
        this.assignedBtId = assignedBtId;
    }

    public String getBieType() {
        return bieType;
    }

    public void setBieType(String bieType) {
        this.bieType = bieType;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
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
}
