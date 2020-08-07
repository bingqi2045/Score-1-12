package org.oagi.srt.repo.component.top_level_asbiep;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateTopLevelAsbiepRequest extends RepositoryRequest {

    private final BigInteger topLevelAsbiepId;
    private final String status;
    private final String version;

    public UpdateTopLevelAsbiepRequest(User user, LocalDateTime localDateTime,
                                       BigInteger topLevelAsbiepId, String status, String version) {
        super(user, localDateTime);
        this.topLevelAsbiepId = topLevelAsbiepId;
        this.status = status;
        this.version = version;
    }

    public BigInteger getTopLevelAsbiepId() {
        return topLevelAsbiepId;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }
}
