package org.oagi.score.repo.component.code_list_value;

import java.math.BigInteger;

public class CreateCodeListValueRepositoryResponse {

    private final String codeListValueManifestId;

    public CreateCodeListValueRepositoryResponse(String codeListValueManifestId) {
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public String getCodeListValueManifestId() {
        return codeListValueManifestId;
    }
}
