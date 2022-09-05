package org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BieEditRef {

    private String asbieId;
    private String basedAsccManifestId;
    private String hashPath;
    private String topLevelAsbiepId;
    private String refTopLevelAsbiepId;
}
