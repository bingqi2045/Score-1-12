package org.oagi.score.repo.component.code_list;

import java.math.BigInteger;

public class CreateCodeListRepositoryResponse {

    private final String codeListManifestId;

    public CreateCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
