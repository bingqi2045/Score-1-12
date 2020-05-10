package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class BieCopyRequest {

    private BigInteger topLevelAbieId;
    private List<BigInteger> bizCtxIds;

}
