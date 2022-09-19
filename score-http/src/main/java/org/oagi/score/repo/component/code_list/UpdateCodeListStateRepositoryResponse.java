package org.oagi.score.repo.component.code_list;

public class UpdateCodeListStateRepositoryResponse {

    private final String codeListManifestId;

    public UpdateCodeListStateRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
