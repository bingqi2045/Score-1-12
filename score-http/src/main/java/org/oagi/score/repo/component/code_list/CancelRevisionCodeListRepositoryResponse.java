package org.oagi.score.repo.component.code_list;

public class CancelRevisionCodeListRepositoryResponse {

    private final String codeListManifestId;

    public CancelRevisionCodeListRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
