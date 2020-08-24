package org.oagi.srt.repo.component.acc;

import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateAccRepositoryRequest extends RepositoryRequest {

    private final BigInteger releaseId;

    private String initialObjectClassTerm = "Object Class Term";
    private OagisComponentType initialComponentType = OagisComponentType.Semantics;
    private String initialDefinition;

    public CreateAccRepositoryRequest(AuthenticatedPrincipal user, BigInteger releaseId) {
        super(user);
        this.releaseId = releaseId;
    }

    public CreateAccRepositoryRequest(AuthenticatedPrincipal user,
                                      LocalDateTime localDateTime,
                                      BigInteger releaseId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }

    public String getInitialObjectClassTerm() {
        return initialObjectClassTerm;
    }

    public void setInitialObjectClassTerm(String initialObjectClassTerm) {
        this.initialObjectClassTerm = initialObjectClassTerm;
    }

    public OagisComponentType getInitialComponentType() {
        return initialComponentType;
    }

    public void setInitialComponentType(OagisComponentType initialComponentType) {
        this.initialComponentType = initialComponentType;
    }

    public String getInitialDefinition() {
        return initialDefinition;
    }

    public void setInitialDefinition(String initialDefinition) {
        this.initialDefinition = initialDefinition;
    }
}
