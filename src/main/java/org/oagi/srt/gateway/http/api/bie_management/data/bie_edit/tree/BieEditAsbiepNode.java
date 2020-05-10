package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsbiepNode extends BieEditNode {

    private BigInteger asbieId = BigInteger.ZERO;
    private BigInteger asccId = BigInteger.ZERO;
    private BigInteger asbiepId = BigInteger.ZERO;
    private BigInteger asccpId = BigInteger.ZERO;
    private BigInteger abieId = BigInteger.ZERO;
    private BigInteger accId = BigInteger.ZERO;

}
