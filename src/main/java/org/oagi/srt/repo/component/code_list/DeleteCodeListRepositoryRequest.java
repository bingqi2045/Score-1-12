package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteCodeListRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;

    public DeleteCodeListRepositoryRequest(User user,
                                           BigInteger codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public DeleteCodeListRepositoryRequest(User user,
                                           LocalDateTime localDateTime,
                                           BigInteger codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }
}
