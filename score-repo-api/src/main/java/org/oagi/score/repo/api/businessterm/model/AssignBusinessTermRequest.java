package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.bie.model.Abie;

import java.math.BigInteger;
import java.util.List;

public class AssignBusinessTermRequest extends Request {

    private List<BieToAssign> biesToAssign;
    private BigInteger businessTermId;
    private String isPrimary;
    private String typeCode;

    public AssignBusinessTermRequest() {
    }

    public AssignBusinessTermRequest(List<BieToAssign> biesToAssign, BigInteger businessTermId, String typeCode, String isPrimary) {
        this.biesToAssign = biesToAssign;
        this.businessTermId = businessTermId;
        this.typeCode = typeCode;
        this.isPrimary = isPrimary;
    }

    public List<BieToAssign> getBiesToAssign() {
        return biesToAssign;
    }

    public void setBiesToAssign(List<BieToAssign> biesToAssign) {
        this.biesToAssign = biesToAssign;
    }

    public BigInteger getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(BigInteger businessTermId) {
        this.businessTermId = businessTermId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }
}
