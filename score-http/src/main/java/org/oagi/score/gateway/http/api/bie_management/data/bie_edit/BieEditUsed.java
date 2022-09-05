package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class BieEditUsed {

    private String hashPath;
    private String bieId;
    private String manifestId;
    private String type;
    private String ownerTopLevelAsbiepId;

}
