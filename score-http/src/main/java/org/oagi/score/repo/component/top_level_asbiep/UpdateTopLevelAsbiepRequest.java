package org.oagi.score.repo.component.top_level_asbiep;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateTopLevelAsbiepRequest extends RepositoryRequest {

    private final String topLevelAsbiepId;
    private final String status;
    private final String version;

    public UpdateTopLevelAsbiepRequest(AuthenticatedPrincipal user, LocalDateTime localDateTime,
                                       String topLevelAsbiepId, String status, String version) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.status = status;
        this.version = version;
    }

    public String getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }
}
