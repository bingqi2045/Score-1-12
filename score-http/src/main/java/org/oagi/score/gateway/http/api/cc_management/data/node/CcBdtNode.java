package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBdtNode extends CcNode {

    private CcType type = CcType.DT;

    private String bdtId;

    private String manifestId;
    private String bccManifestId;
    private BigInteger basedManifestId;
    private String den;
    private String prevBccpId;
    private String nextBccpId;

    @Override
    public String getId() {
        return bdtId;
    }
}
