package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAsccNode extends CcNode {

    private int seqKey;
    private String asccId;
    private String manifestId;
    private String fromAccManifestId;
    private String toAsccpManifestId;
    private BigInteger cardinalityMin = BigInteger.ZERO;
    private BigInteger cardinalityMax = BigInteger.ZERO;
    private boolean deprecated;

    @Override
    public String getId() {
        return asccId;
    }

}