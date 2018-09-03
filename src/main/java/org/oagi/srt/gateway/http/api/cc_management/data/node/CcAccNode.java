package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAccNode extends CcNode {

    private long accId;
    private Long basedAccId;
    private Long currentAccId;
    private int oagisComponentType;
    private boolean group;

}
