package org.oagi.score.repo.component.code_list;

import java.math.BigInteger;

public class UpdateCodeListOwnerRepositoryResponse {

    private final String codeListManifestId;

    public UpdateCodeListOwnerRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
