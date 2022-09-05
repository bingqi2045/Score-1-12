package org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBbiepNode extends BieEditNode {

    private String bbieId;
    private String bccManifestId;
    private String bbiepId;
    private String bccpManifestId;
    private String bdtManifestId;
    private boolean attribute;

}
