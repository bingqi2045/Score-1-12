package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditUsed {

    private String hashPath;
    private BigInteger topLevelAsbiepId;
    private String type;
    private boolean isUsed;

}