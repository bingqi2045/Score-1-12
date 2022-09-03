package org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsbiepNode extends BieEditNode {

    private String asbieId;
    private BigInteger asccManifestId = BigInteger.ZERO;
    private String asbiepId;
    private BigInteger asccpManifestId = BigInteger.ZERO;
    private String abieId;
    private BigInteger accManifestId = BigInteger.ZERO;

}
