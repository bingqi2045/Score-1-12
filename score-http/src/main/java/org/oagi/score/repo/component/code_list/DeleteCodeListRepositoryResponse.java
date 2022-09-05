package org.oagi.score.repo.component.code_list;

import java.math.BigInteger;

public class DeleteCodeListRepositoryResponse {

    private final String codeListManifestId;

    public DeleteCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
