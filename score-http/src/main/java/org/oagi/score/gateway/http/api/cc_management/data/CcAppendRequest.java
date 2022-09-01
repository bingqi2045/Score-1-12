package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcAppendRequest {
    private String releaseId;
    private BigInteger accManifestId;
    private BigInteger asccpManifestId;
    private BigInteger bccpManifestId;
    private boolean attribute;
    private int pos;
}
