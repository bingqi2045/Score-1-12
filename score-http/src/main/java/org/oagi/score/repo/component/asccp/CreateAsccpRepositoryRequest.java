package org.oagi.score.repo.component.asccp;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateAsccpRepositoryRequest extends RepositoryRequest {

    private final BigInteger roleOfAccManifestId;
    private final BigInteger releaseId;

    private String initialPropertyTerm = "Property Term";
    private boolean reusable = true;
    private String definition;
    private CcState initialState = CcState.WIP;

    public CreateAsccpRepositoryRequest(AuthenticatedPrincipal user,
                                        BigInteger roleOfAccManifestId, BigInteger releaseId) {
        super(user);
        this.roleOfAccManifestId = roleOfAccManifestId;
        this.releaseId = releaseId;
    }

    public CreateAsccpRepositoryRequest(AuthenticatedPrincipal user,
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

    public boolean isReusable() {
        return reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public CcState getInitialState() {
        return initialState;
    }

    public void setInitialState(CcState initialState) {
        this.initialState = initialState;
    }
}