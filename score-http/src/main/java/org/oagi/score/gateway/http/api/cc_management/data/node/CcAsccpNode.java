package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.data.SeqKeySupportable;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAsccpNode extends CcNode implements SeqKeySupportable {

    private CcType type = CcType.ASCCP;

    private String asccpId;
    private String asccId;
    private String roleOfAccId;
    private int seqKey;
    private String manifestId;
    private String asccManifestId;
    private String asccpType;

    @Override
    public String getId() {
        return asccpId;
    }
}
