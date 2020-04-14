package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class CodeListForListRequest {

    private long releaseId;
    private String name;
    private List<String> states = Collections.emptyList();
    private Boolean extensible;

    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

}
