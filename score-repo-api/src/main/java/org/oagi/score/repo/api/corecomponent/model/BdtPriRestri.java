package org.oagi.score.repo.api.corecomponent.model;

import java.io.Serializable;
import java.math.BigInteger;

public class BdtPriRestri implements Serializable {

    private String bdtPriRestriId;
    private String bdtId;
    private String xbtId;
    private String xbtName;
    private String cdtAwdPriXpsTypeMapId;
    private String codeListId;
    private String agencyIdListId;
    private boolean isDefault;

    public String getBdtPriRestriId() {
        return bdtPriRestriId;
    }

    public void setBdtPriRestriId(String bdtPriRestriId) {
        this.bdtPriRestriId = bdtPriRestriId;
    }

    public String getBdtId() {
        return bdtId;
    }

    public void setBdtId(String bdtId) {
        this.bdtId = bdtId;
    }

    public String getXbtId() {
        return xbtId;
    }

    public void setXbtId(String xbtId) {
        this.xbtId = xbtId;
    }

    public String getXbtName() {
        return xbtName;
    }

    public void setXbtName(String xbtName) {
        this.xbtName = xbtName;
    }

    public String getCdtAwdPriXpsTypeMapId() {
        return cdtAwdPriXpsTypeMapId;
    }

    public void setCdtAwdPriXpsTypeMapId(String cdtAwdPriXpsTypeMapId) {
        this.cdtAwdPriXpsTypeMapId = cdtAwdPriXpsTypeMapId;
    }

    public String getCodeListId() {
        return codeListId;
    }

    public void setCodeListId(String codeListId) {
        this.codeListId = codeListId;
    }

    public String getAgencyIdListId() {
        return agencyIdListId;
    }

    public void setAgencyIdListId(String agencyIdListId) {
        this.agencyIdListId = agencyIdListId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
