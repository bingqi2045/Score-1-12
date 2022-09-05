package org.oagi.score.repo.component.code_list;

public class RestoreCodeListRepositoryResponse {

    private final String codeListManifestId;

    public RestoreCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
