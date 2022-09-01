package org.oagi.score.repo.component.bcc;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.log.model.LogAction;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateBccRepositoryRequest extends RepositoryRequest {

    private final String releaseId;
    private final BigInteger accManifestId;
    private final BigInteger bccpManifestId;
    private final boolean attribute;
    private int pos = -1;
    private String logHash;
    private LogAction logAction;

    private CcState initialState = CcState.WIP;

    public CreateBccRepositoryRequest(AuthenticatedPrincipal user,
                                      String releaseId,
                                      BigInteger accManifestId,
                                      BigInteger bccpManifestId,
                                      boolean attribute) {
        super(user);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.bccpManifestId = bccpManifestId;
        this.attribute = attribute;
    }

    public CreateBccRepositoryRequest(AuthenticatedPrincipal user,
                                      LocalDateTime localDateTime,
                                      String releaseId,
                                      BigInteger accManifestId,
                                      BigInteger bccpManifestId,
                                      boolean attribute) {
        super(user, localDateTime);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.bccpManifestId = bccpManifestId;
        this.attribute = attribute;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
    }

    public boolean isAttribute() {
        return attribute;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public CcState getInitialState() {
        return initialState;
    }

    public void setInitialState(CcState initialState) {
        this.initialState = initialState;
    }

    public String getLogHash() {
        return logHash;
    }

    public void setLogHash(String logHash) {
        this.logHash = logHash;
    }

    public LogAction getLogAction() {
        return logAction;
    }

    public void setLogAction(LogAction logAction) {
        this.logAction = logAction;
    }
}
