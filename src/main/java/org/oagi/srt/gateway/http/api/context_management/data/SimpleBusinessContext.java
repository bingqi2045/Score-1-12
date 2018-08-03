package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleBusinessContext {

    private long bizCtxId;
    private String name;
    private Date lastUpdateTimestamp;

}
