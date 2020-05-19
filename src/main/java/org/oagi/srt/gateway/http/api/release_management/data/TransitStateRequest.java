package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TransitStateRequest {

    private BigInteger releaseId;
    private String state;
}
