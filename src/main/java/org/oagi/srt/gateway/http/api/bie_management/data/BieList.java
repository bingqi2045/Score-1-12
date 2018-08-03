package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class BieList {

    private long topLevelAbieId;
    private String propertyTerm;
    private String releaseNum;
    private long bizCtxId;
    private String bizCtxName;
    private String owner;
    private String version;
    private String status;
    private Date lastUpdateTimestamp;
    private Object state;

}
