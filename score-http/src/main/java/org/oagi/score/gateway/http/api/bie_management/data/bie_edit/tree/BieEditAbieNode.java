package org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.BieEditNode;
import org.oagi.score.service.common.data.AccessPrivilege;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAbieNode extends BieEditNode {

    private String asbiepId;
    private String abieId;
    private String asccpManifestId;
    private String accManifestId;
    private String ownerUserId;
    private String loginId;
    private String releaseNum;
    private AccessPrivilege access;

}
