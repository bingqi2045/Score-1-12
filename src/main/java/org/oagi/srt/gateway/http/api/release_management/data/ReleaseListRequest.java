package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;
import org.oagi.srt.data.ReleaseState;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class ReleaseListRequest {

    private String releaseNum;
    private String namespace;
    private List<String> excludes = Collections.emptyList();
    private List<ReleaseState> states = Collections.emptyList();
    private List<String> updaterLoginIds = Collections.emptyList();
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest;

}
