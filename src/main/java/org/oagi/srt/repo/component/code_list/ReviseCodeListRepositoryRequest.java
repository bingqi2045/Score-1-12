package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class ReviseCodeListRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;

    public ReviseCodeListRepositoryRequest(User user,
                                           BigInteger codeListManifestId,
                                           LocalDateTime localDateTime) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }
}
