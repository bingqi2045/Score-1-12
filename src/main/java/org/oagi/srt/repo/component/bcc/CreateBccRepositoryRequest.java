package org.oagi.srt.repo.component.bcc;

import org.oagi.srt.data.RepositoryRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateBccRepositoryRequest extends RepositoryRequest {

    private final BigInteger releaseId;
    private final BigInteger accManifestId;
    private final BigInteger bccpManifestId;
    private int pos = -1;

    private CcState initialState = CcState.WIP;

    public CreateBccRepositoryRequest(User user,
                                      BigInteger releaseId,
                                      BigInteger accManifestId,
                                      BigInteger bccpManifestId) {
        super(user);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.bccpManifestId = bccpManifestId;
    }

    public CreateBccRepositoryRequest(User user,
                                      LocalDateTime localDateTime,
                                      BigInteger releaseId,
                                      BigInteger accManifestId,
                                      BigInteger bccpManifestId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
        this.accManifestId = accManifestId;
        this.bccpManifestId = bccpManifestId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }

    public BigInteger getAccManifestId() {
        return accManifestId;
    }

    public BigInteger getBccpManifestId() {
        return bccpManifestId;
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
}
