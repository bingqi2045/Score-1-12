package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CcUpdateResponse {

    private List<CcAccNodeDetail> accNodeResults;
    private List<CcAsccpNodeDetail> asccpNodeResults;
    private List<CcBccpNodeDetail> bccpNodeResults;
    private List<CcBdtScNodeDetail> bdtScNodeResults;
}
