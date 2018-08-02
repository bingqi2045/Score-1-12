package org.oagi.srt.gateway.http.bie_management.bie_edit;

import java.util.Collections;
import java.util.List;

public class BdtScPriRestri {
    private long bdtScId;;
    private long bdtScPriRestriId;
    private long codeListId;
    private long agencyIdListId;

    private List<Xbt> xbtList = Collections.emptyList();
    private List<CodeList> codeLists = Collections.emptyList();
    private List<AgencyIdList> agencyIdLists = Collections.emptyList();

    public long getBdtScId() {
        return bdtScId;
    }

    public void setBdtScId(long bdtScId) {
        this.bdtScId = bdtScId;
    }

    public long getBdtScPriRestriId() {
        return bdtScPriRestriId;
    }

    public void setBdtScPriRestriId(long bdtScPriRestriId) {
        this.bdtScPriRestriId = bdtScPriRestriId;
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
