package org.oagi.score.repo.component.bbiep;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpsertBbiepRequest extends RepositoryRequest {

    private final String topLevelAsbiepId;
    private final BbiepNode.Bbiep bbiep;

    public UpsertBbiepRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime,
                              String topLevelAsbiepId, BbiepNode.Bbiep bbiep) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.bbiep = bbiep;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public BbiepNode.Bbiep getBbiep() {
        return bbiep;
    }
}
