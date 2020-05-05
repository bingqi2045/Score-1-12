package org.oagi.srt.repo.component.asccp;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger roleOfAccManifestId;
    private final BigInteger releaseId;

    private String initialPropertyTerm = "Property Term";

    public CreateAsccpRepositoryRequest(User user,
                                        BigInteger roleOfAccManifestId, BigInteger releaseId) {
        super(user);
        this.roleOfAccManifestId = roleOfAccManifestId;
        this.releaseId = releaseId;
    }

    public CreateAsccpRepositoryRequest(User user,
                                        LocalDateTime localDateTime,
                                        BigInteger roleOfAccManifestId, BigInteger releaseId) {
        super(user, localDateTime);
        this.roleOfAccManifestId = roleOfAccManifestId;
        this.releaseId = releaseId;
    }

    public BigInteger getRoleOfAccManifestId() {
        return roleOfAccManifestId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }

    public String getInitialPropertyTerm() {
        return initialPropertyTerm;
    }

    public void setInitialPropertyTerm(String initialPropertyTerm) {
        this.initialPropertyTerm = initialPropertyTerm;
    }
}
