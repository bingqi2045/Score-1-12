package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAbieNode extends BieEditNode {

    private BigInteger asbiepId;
    private BigInteger abieId;
    private BigInteger asccpManifestId;
    private BigInteger accManifestId;
    private BigInteger ownerUserId;
    private String loginId;
    private String releaseNum;
    private BieState topLevelAbieState;
    private String access;

}
