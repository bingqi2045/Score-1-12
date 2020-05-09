package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNodeDetail;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNodeDetail;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class ExtensionUpdateRequest {

    private BigInteger manifestId;
    private List<CcAsccpNodeDetail> asccpDetails = Collections.emptyList();
    private List<CcBccpNodeDetail> bccpDetails = Collections.emptyList();

}
