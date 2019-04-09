package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class BieListRequest {

    private String name;
    private List<BieState> states = Collections.emptyList();

    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest;

}
