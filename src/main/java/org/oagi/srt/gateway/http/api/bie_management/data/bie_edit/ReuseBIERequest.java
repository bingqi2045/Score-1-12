package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ReuseBIERequest {

    private BigInteger topLevelAbieId;
    private BigInteger asccpManifestId;
    private String asbiepHashPath;
    private BigInteger reuseTopLevelAbieId;
}
