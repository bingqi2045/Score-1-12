package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class OverrideBIERequest {

    private BigInteger topLevelAbieId;

    private BigInteger accManifestId;
    private String abieHashPath;

    private BigInteger asccpManifestId;
    private String asbiepHashPath;

    private BigInteger overrideTopLevelAbieId;
}
