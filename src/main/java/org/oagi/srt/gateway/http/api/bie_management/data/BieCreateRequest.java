package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

@Data
public class BieCreateRequest {

    private long asccpId;
    private long releaseId;
    private long bizCtxId;

}
