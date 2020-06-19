package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditUsed {

    private String hashPath;
    private BigInteger topLevelAbieId;
    private String type;
    private boolean isUsed;

}
