package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNodeDetail;

@Data
public class CcActionRequest {
    private String action;
    private String type;
    private Long manifestId;
    private Long id;
    private CcAsccpNodeDetail asccpNodeDetail;
}
