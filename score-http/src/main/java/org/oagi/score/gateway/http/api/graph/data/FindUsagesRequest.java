package org.oagi.score.gateway.http.api.graph.data;

import lombok.Data;

@Data
public class FindUsagesRequest {

    private String type;
    private String manifestId;

}
