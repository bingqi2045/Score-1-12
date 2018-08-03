package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BdtScPriRestri {

    private long bdtScId;;
    private long bdtScPriRestriId;
    private long codeListId;
    private long agencyIdListId;

    private List<Xbt> xbtList = Collections.emptyList();
    private List<CodeList> codeLists = Collections.emptyList();
    private List<AgencyIdList> agencyIdLists = Collections.emptyList();

}
