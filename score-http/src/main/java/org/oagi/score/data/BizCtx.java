package org.oagi.score.data;

import lombok.Data;

import java.util.Date;

@Data
public class BizCtx {

    private String bizCtxId;
    private String guid;
    private String name;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
}
