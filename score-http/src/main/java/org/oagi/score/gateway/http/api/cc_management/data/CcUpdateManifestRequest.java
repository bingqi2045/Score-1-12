package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcUpdateManifestRequest {
    private String accManifestId;
    private String bdtManifestId;
}
