package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BdtPriRestri {

    private long bdtId;
    private String bdtDen;
    private long bdtPriRestriId;
    private long codeListId;
    private long agencyIdListId;

    private List<Xbt> xbtList = Collections.emptyList();
    private List<CodeList> codeLists = Collections.emptyList();
    private List<AgencyIdList> agencyIdLists = Collections.emptyList();

}
