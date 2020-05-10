package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.SeqKeySupportable;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBccpNode extends CcNode implements SeqKeySupportable {

    private String type = "bccp";

    private BigInteger bccId = BigInteger.ZERO;
    private boolean attribute;
    private int seqKey;

    private BigInteger bccpId = BigInteger.ZERO;
    private BigInteger bdtId = BigInteger.ZERO;
    private BigInteger manifestId = BigInteger.ZERO;
    private BigInteger bccManifestId = BigInteger.ZERO;
    private BigInteger prevBccpId;
    private BigInteger nextBccpId;

    @Override
    public BigInteger getId() {
        return bccpId;
    }
}
