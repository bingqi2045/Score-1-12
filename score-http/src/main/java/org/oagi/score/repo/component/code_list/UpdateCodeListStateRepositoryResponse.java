package org.oagi.score.repo.component.code_list;

import java.math.BigInteger;

public class UpdateCodeListStateRepositoryResponse {

    private final String codeListManifestId;

    public UpdateCodeListStateRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
