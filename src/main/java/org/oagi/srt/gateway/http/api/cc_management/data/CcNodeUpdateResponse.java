package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcNodeUpdateResponse {

    private String type;
    private BigInteger manifestId;
    private String state;
    private String access;
}
