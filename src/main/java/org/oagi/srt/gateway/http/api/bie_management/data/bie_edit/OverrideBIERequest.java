package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class OverrideBIERequest {

    private BigInteger topLevelAbieId;
    private BigInteger asccpManifestId;
    private String hashPath;
    private BigInteger overrideTopLevelAbieId;
}
