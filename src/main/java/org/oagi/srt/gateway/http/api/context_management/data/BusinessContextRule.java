package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class BusinessContextRule implements Serializable {

    private long bizCtxRuleId;
    private long fromBizCtxId;
    private long topLevelBieId;

}
