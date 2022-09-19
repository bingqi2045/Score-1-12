package org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsbiepNode extends BieEditNode {

    private String asbieId;
    private String asccManifestId;
    private String asbiepId;
    private String asccpManifestId;
    private String abieId;
    private String accManifestId;

}
