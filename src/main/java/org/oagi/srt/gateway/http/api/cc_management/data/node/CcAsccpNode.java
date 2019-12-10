package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.SeqKeySupportable;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAsccpNode extends CcNode implements SeqKeySupportable {

    private String type = "asccp";

    private long asccpId;
    private long asccId;
    private int roleOfAccId;
    private int seqKey;
    private long manifestId;
    private long asccManifestId;

    @Override
    public long getId() {
        return asccpId;
    }
}
