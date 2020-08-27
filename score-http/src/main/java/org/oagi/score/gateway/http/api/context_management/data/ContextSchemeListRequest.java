package org.oagi.score.gateway.http.api.context_management.data;

import lombok.Data;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class ContextSchemeListRequest {

    private String name;
    private List<String> updaterLoginIds = Collections.emptyList();
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

}