package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;

import java.util.List;

@Data
public class BieCopyRequest {

    private String topLevelAsbiepId;
    private List<String> bizCtxIds;

}
