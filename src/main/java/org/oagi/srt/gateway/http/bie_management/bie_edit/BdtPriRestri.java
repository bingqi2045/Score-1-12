package org.oagi.srt.gateway.http.bie_management.bie_edit;

import java.util.Collections;
import java.util.List;

public class BdtPriRestri {
    private long bdtId;
    private String bdtDen;
    private long bdtPriRestriId;
    private long codeListId;
    private long agencyIdListId;

    private List<Xbt> xbtList = Collections.emptyList();
    private List<CodeList> codeLists = Collections.emptyList();
    private List<AgencyIdList> agencyIdLists = Collections.emptyList();

    public long getBdtId() {
        return bdtId;
    }

    public void setBdtId(long bdtId) {
        this.bdtId = bdtId;
    }

    public String getBdtDen() {
        return bdtDen;
    }

    public void setBdtDen(String bdtDen) {
        this.bdtDen = bdtDen;
    }

    public long getBdtPriRestriId() {
        return bdtPriRestriId;
    }

    public void setBdtPriRestriId(long bdtPriRestriId) {
        this.bdtPriRestriId = bdtPriRestriId;
    }

    public long getCodeListId() {
        return codeListId;
    }

    public void setCodeListId(long codeListId) {
        this.codeListId = codeListId;
    }

    public long getAgencyIdListId() {
        return agencyIdListId;
    }

    public void setAgencyIdListId(long agencyIdListId) {
        this.agencyIdListId = agencyIdListId;
    }

    public List<Xbt> getXbtList() {
        return xbtList;
    }

    public void setXbtList(List<Xbt> xbtList) {
        this.xbtList = xbtList;
    }

    public List<CodeList> getCodeLists() {
        return codeLists;
    }

    public void setCodeLists(List<CodeList> codeLists) {
        this.codeLists = codeLists;
    }

    public List<AgencyIdList> getAgencyIdLists() {
        return agencyIdLists;
    }

    public void setAgencyIdLists(List<AgencyIdList> agencyIdLists) {
        this.agencyIdLists = agencyIdLists;
    }
}
