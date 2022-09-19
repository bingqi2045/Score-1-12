package org.oagi.score.repo.component.code_list;

public class DeleteCodeListRepositoryResponse {

    private final String codeListManifestId;

    public DeleteCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
