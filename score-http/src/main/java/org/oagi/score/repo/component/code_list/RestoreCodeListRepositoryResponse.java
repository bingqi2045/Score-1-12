package org.oagi.score.repo.component.code_list;

import java.math.BigInteger;

public class RestoreCodeListRepositoryResponse {

    private final String codeListManifestId;

    public RestoreCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
