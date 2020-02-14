package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBccNode extends CcNode {

    private int seqKey;
    private long bccId;
    private long manifestId;
    private long toBccpManifestId;
    private int entityType;
    private long cardinalityMin;
    private long cardinalityMax;
    private long nillable;

    @Override
    public long getId() {
        return bccId;
    }
}
