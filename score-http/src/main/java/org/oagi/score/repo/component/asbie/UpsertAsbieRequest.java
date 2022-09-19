package org.oagi.score.repo.component.asbie;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

public class UpsertAsbieRequest extends RepositoryRequest {

    private final String topLevelAsbiepId;
    private final AsbieNode.Asbie asbie;

    public UpsertAsbieRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime,
                              String topLevelAsbiepId, AsbieNode.Asbie asbie) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.asbie = asbie;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public AsbieNode.Asbie getAsbie() {
        return asbie;
    }
}
