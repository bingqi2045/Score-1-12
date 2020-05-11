package org.oagi.srt.repo.component.code_list_value;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteCodeListValueRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListValueManifestId;

    public DeleteCodeListValueRepositoryRequest(User user,
                                                BigInteger codeListValueManifestId) {
        super(user);
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public DeleteCodeListValueRepositoryRequest(User user,
                                                LocalDateTime localDateTime,
                                                BigInteger codeListValueManifestId) {
        super(user, localDateTime);
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public BigInteger getCodeListValueManifestId() {
        return codeListValueManifestId;
    }
}
