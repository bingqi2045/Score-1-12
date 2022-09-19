package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcNodeUpdateResponse {

    private CcType type;
    private String manifestId;
    private String state;
    private String access;
    private String den;
}
