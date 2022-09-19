package org.oagi.score.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.service.common.data.CcState;

import java.time.LocalDateTime;

@Data
public class SummaryCcExt {

    private String accId;
    private String guid;
    private String objectClassTerm;
    private CcState state;
    private LocalDateTime lastUpdateTimestamp;
    private String lastUpdateUser;

    private String ownerUsername;
    private String ownerUserId;

    private String topLevelAsbiepId;
    private BieState bieState;
    private String propertyTerm;
    private String associationPropertyTerm;
    private int seqKey;

}
