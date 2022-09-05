package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcBdtCreateRequest {

    private String releaseId;
    private String bdtManifestId;
    private String specId;

}
