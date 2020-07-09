package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;

public class DiscardRevisionCodeListRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;

    public DiscardRevisionCodeListRepositoryRequest(User user,
                                                    BigInteger codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }
}
