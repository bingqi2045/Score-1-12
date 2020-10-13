package org.oagi.score.gateway.http.api.code_list_management.data;

import lombok.Data;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class CodeListForListRequest {

    private long releaseId;
    private String name;
    private String definition;
    private String module;
    private List<CcState> states = Collections.emptyList();
    private Boolean deprecated;
    private Boolean extensible;

    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

}
