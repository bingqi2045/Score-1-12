package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.data.SeqKeySupportable;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBccpNode extends CcNode implements SeqKeySupportable {

    private CcType type = CcType.BCCP;

    private String bccId;
    private boolean attribute;
    private int seqKey;

    private String bccpId;
    private String bdtId;
    private String manifestId;
    private String bccManifestId;
    private BigInteger prevBccpId;
    private BigInteger nextBccpId;

    @Override
    public String getId() {
        return bccpId;
    }
}
