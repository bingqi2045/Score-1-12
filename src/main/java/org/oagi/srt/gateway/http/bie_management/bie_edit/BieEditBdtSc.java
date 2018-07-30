package org.oagi.srt.gateway.http.bie_management.bie_edit;

public class BieEditBdtSc {

    private long dtScId;
    private String guid;
    private String propertyTerm;
    private String representationTerm;
    private long ownerDtId;

    public long getDtScId() {
        return dtScId;
    }

    public void setDtScId(long dtScId) {
        this.dtScId = dtScId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public void setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
    }

    public String getRepresentationTerm() {
        return representationTerm;
    }

    public void setRepresentationTerm(String representationTerm) {
        this.representationTerm = representationTerm;
    }

    public long getOwnerDtId() {
        return ownerDtId;
    }

    public void setOwnerDtId(long ownerDtId) {
        this.ownerDtId = ownerDtId;
    }
}
