package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBdtScNode extends CcNode {

    private String type = "bdt_sc";

    private BigInteger bdtScId = BigInteger.ZERO;
    private BigInteger manifestId = BigInteger.ZERO;

    @Override
    public BigInteger getId() {
        return bdtScId;
    }
}
