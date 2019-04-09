package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BusinessContextListRequest {

    private String name;

    private List<String> updaterLoginIds;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest;

}
