package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBbiepNode extends BieEditNode {

    private BigInteger bbieId = BigInteger.ZERO;
    private BigInteger bccId = BigInteger.ZERO;
    private BigInteger bbiepId = BigInteger.ZERO;
    private BigInteger bccpId = BigInteger.ZERO;
    private BigInteger bdtId = BigInteger.ZERO;
    private boolean attribute;

}
