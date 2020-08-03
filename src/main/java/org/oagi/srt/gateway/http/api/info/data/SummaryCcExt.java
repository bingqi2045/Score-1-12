package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.srt.data.BieState;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class SummaryCcExt {

    private BigInteger accId;
    private String guid;
    private String objectClassTerm;
    private CcState state;
    private LocalDateTime lastUpdateTimestamp;
    private String lastUpdateUser;

    private String ownerUsername;
    private BigInteger ownerUserId;

    private BigInteger topLevelAsbiepId;
    private BieState bieState;
    private String propertyTerm;
    private String associationPropertyTerm;
    private int seqKey;

}
