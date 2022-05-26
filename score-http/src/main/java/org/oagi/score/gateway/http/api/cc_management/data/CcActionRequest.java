package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.score.gateway.http.api.cc_management.data.node.CcAsccpNodeDetail;

import java.math.BigInteger;

@Data
public class CcActionRequest {
    private String action;
    private String type;
    private boolean attribute;
    private BigInteger manifestId = BigInteger.ZERO;
    private BigInteger id = BigInteger.ZERO;
    private CcAsccpNodeDetail asccpNodeDetail;
}
