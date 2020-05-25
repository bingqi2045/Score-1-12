package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcSetBaseAccRequest {
    private BigInteger basedAccManifestId;
}
