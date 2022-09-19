package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class ResetDetailBIERequest {

    private String topLevelAsbiepId;
    private String bieType;
    private String path;
}
