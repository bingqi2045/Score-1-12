package org.oagi.srt.gateway.http.api.namespace_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Namespace {

    private BigInteger namespaceId = BigInteger.ZERO;
    private String uri;
    private String prefix;
    private String description;
    private BigInteger ownerUserId = BigInteger.ZERO;
}
