package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class DeleteBusinessContextRequest {

    private List<BigInteger> bizCtxIds = Collections.emptyList();
}
