package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcRevisionResponse {
    String type;
    Long ccId;
    Boolean isDeprecated;
    Boolean isNillable;
    String name;
    String fixedValue;
}
