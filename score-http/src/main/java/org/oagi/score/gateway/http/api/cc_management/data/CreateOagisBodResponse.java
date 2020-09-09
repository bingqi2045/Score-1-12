package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CreateOagisBodResponse {

    private BigInteger verbAsccpId;

    private BigInteger nounAsccpId;

}
