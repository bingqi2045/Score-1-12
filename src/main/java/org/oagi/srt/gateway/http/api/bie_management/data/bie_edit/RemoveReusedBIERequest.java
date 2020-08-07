package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RemoveReusedBIERequest {

    private BigInteger topLevelAsbiepId;
    private BigInteger asccpManifestId;
    private String asbieHashPath;
}
