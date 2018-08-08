package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

@Data
public class BieCopyRequest {

    private long topLevelAbieId;
    private long bizCtxId;

}
