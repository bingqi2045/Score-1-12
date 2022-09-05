package org.oagi.score.repo.component.code_list_value;

public class DeleteCodeListValueRepositoryResponse {

    private final String codeListValueManifestId;

    public DeleteCodeListValueRepositoryResponse(String codeListValueManifestId) {
        this.codeListValueManifestId = codeListValueManifestId;
    }

    public String getCodeListValueManifestId() {
        return codeListValueManifestId;
    }
}
