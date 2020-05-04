package org.oagi.srt.repo.domain;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateBccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger bdtManifestId;
    private final BigInteger releaseId;

    private String initialPropertyTerm = "Property Term";

    public CreateBccpRepositoryRequest(User user,
                                       BigInteger bdtManifestId, BigInteger releaseId) {
        super(user);
        this.bdtManifestId = bdtManifestId;
        this.releaseId = releaseId;
    }

    public CreateBccpRepositoryRequest(User user,
                                       LocalDateTime localDateTime,
                                       BigInteger bdtManifestId, BigInteger releaseId) {
        super(user, localDateTime);
        this.bdtManifestId = bdtManifestId;
        this.releaseId = releaseId;
    }

    public BigInteger getBdtManifestId() {
        return bdtManifestId;
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
