package org.oagi.score.repo.component.code_list;

public class ReviseCodeListRepositoryResponse {

    private final String codeListManifestId;

    public ReviseCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
