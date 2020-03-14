package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class CcListRequest {

    private long releaseId;
    private CcListTypes types;
    private List<CcState> states;
    private Boolean deprecated;
    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private String den;
    private String definition;
    private String module;
    private String componentTypes;
    private List<String> excludes;

    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

    private Map<Long, String> usernameMap;
}
