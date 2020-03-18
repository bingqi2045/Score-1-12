package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;

@Data
public class CcRevisionResponse {
    CcNode ccNode;
}
