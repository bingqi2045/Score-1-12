package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBccNode extends CcNode {

    private int seqKey;
    private long bccId;
    private long toBccpId;
    private int entityType;

    @Override
    public long getId() {
        return bccId;
    }
}
