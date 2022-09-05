package org.oagi.score.repo.component.code_list;

public class CreateCodeListRepositoryResponse {

    private final String codeListManifestId;

    public CreateCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
