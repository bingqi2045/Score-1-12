package org.oagi.score.repo.component.code_list_value;

import java.math.BigInteger;

public class UpdateCodeListValueRepositoryResponse {

    private final String codeListValueManifestId;

    public UpdateCodeListValueRepositoryResponse(String codeListValueManifestId) {
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public String getBccpManifestId() {
        return codeListValueManifestId;
    }
}
