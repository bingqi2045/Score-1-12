package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;
import org.jooq.types.ULong;

import java.math.BigInteger;
import java.util.List;

@Data
public class CcRefactorValidationResponse {

    private String type;

    private BigInteger manifestId;

    private List<CcList> blockers;
}
