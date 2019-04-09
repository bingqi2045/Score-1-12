package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;
import org.oagi.srt.data.BieState;

import java.util.Date;

@Data
public class BieList {

    private long topLevelAbieId;
    private String propertyTerm;
    private String guid;
    private String releaseNum;
    private Long bizCtxId;
    private String bizCtxName;

    private String owner;
    private long ownerUserId;
    private String access;

    private String version;
    private String status;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
    private int rawState;
    private BieState state;

}
