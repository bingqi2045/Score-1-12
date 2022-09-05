package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcBdtScNode extends CcNode {

    private CcType type = CcType.DT_SC;

    private String bdtScId;
    private String manifestId;

    @Override
    public String getId() {
        return bdtScId;
    }
}
