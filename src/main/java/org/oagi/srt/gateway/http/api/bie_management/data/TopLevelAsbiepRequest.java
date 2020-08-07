package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class TopLevelAsbiepRequest {

    private BigInteger topLevelAsbiepId;
    private String version;
    private String status;

}
