package org.oagi.score.repo.component.bbie;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpsertBbieRequest extends RepositoryRequest {

    private final String topLevelAsbiepId;
    private final BbieNode.Bbie bbie;

    public UpsertBbieRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime,
                             String topLevelAsbiepId, BbieNode.Bbie bbie) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.bbie = bbie;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public BbieNode.Bbie getBbie() {
        return bbie;
    }
}
