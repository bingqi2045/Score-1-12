package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.List;

@Data
public class CcRevisionResponse {
    String type;
    Long ccId;
    Boolean isDeprecated;
    Boolean isNillable;
    Boolean isAbstract;
    Boolean hasBaseCc;
    String name;
    String fixedValue;
    List<String> associationKeys;
}
