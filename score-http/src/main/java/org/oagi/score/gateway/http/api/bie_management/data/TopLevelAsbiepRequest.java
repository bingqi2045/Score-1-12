package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TopLevelAsbiepRequest {

    private String topLevelAsbiepId;
    private String version;
    private String status;

}
