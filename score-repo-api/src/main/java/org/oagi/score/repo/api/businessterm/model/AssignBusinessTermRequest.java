package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Request;

import java.util.List;

public class AssignBusinessTermRequest extends Request {

    private List<BieToAssign> biesToAssign;
    private String businessTermId;
    private String primaryIndicator;
    private Boolean isPrimary;

    private String typeCode;

    public AssignBusinessTermRequest() {
    }

    public AssignBusinessTermRequest(List<BieToAssign> biesToAssign, String businessTermId, String typeCode,
                                     String primaryIndicator, Boolean isPrimary) {
        this.biesToAssign = biesToAssign;
        this.businessTermId = businessTermId;
        this.typeCode = typeCode;
        this.primaryIndicator = primaryIndicator;
        this.isPrimary = isPrimary;
    }

    public List<BieToAssign> getBiesToAssign() {
        return biesToAssign;
    }

    public void setBiesToAssign(List<BieToAssign> biesToAssign) {
        this.biesToAssign = biesToAssign;
    }

    public String getBusinessTermId() {
        return businessTermId;
    }

    public void setBusinessTermId(String businessTermId) {
        this.businessTermId = businessTermId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getPrimaryIndicator() {
        return primaryIndicator;
    }

    public void setPrimaryIndicator(String primaryIndicator) {
        this.primaryIndicator = primaryIndicator;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String toString() {
        return "AssignBusinessTermRequest{" +
                "biesToAssign=" + biesToAssign +
                ", businessTermId=" + businessTermId +
                ", primaryIndicator='" + primaryIndicator + '\'' +
                ", isPrimary=" + isPrimary +
                ", typeCode='" + typeCode + '\'' +
                '}';
    }
}
