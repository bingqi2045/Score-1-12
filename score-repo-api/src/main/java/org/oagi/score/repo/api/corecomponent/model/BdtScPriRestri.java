package org.oagi.score.repo.api.corecomponent.model;

import java.io.Serializable;
import java.math.BigInteger;

public class BdtScPriRestri implements Serializable {

    private String bdtScPriRestriId;
    private String bdtScId;
    private String xbtId;
    private String xbtName;
    private String cdtScAwdPriXpsTypeMapId;
    private String codeListId;
    private String agencyIdListId;
    private boolean isDefault;

    public String getBdtScPriRestriId() {
        return bdtScPriRestriId;
    }

    public void setBdtScPriRestriId(String bdtScPriRestriId) {
        this.bdtScPriRestriId = bdtScPriRestriId;
    }

    public String getBdtScId() {
        return bdtScId;
    }

    public void setBdtScId(String bdtScId) {
        this.bdtScId = bdtScId;
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

    public String getCdtScAwdPriXpsTypeMapId() {
        return cdtScAwdPriXpsTypeMapId;
    }

    public void setCdtScAwdPriXpsTypeMapId(String cdtScAwdPriXpsTypeMapId) {
        this.cdtScAwdPriXpsTypeMapId = cdtScAwdPriXpsTypeMapId;
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
