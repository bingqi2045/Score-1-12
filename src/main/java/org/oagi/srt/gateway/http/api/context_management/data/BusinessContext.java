package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class BusinessContext {

    private long bizCtxId;
    private String guid;
    private String name;
    private Date lastUpdateTimestamp;
    private List<BusinessContextValue> bizCtxValues = Collections.emptyList();

}
