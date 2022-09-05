package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class RemoveReusedBIERequest {

    private String topLevelAsbiepId;
    private String asbieHashPath;
}
