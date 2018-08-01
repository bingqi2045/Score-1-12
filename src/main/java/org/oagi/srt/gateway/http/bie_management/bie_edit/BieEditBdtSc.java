package org.oagi.srt.gateway.http.bie_management.bie_edit;

import org.oagi.srt.gateway.http.helper.Utility;

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

    public String getName() {
        String name;
        if (getRepresentationTerm().equalsIgnoreCase("Text") ||
                getPropertyTerm().contains(getRepresentationTerm())) {
            name = Utility.spaceSeparator(getPropertyTerm());
        } else {
            name = Utility.spaceSeparator(getPropertyTerm().concat(getRepresentationTerm()));
        }
        return name;
    }
}
