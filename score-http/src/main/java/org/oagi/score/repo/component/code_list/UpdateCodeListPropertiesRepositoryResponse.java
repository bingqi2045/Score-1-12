package org.oagi.score.repo.component.code_list;

public class UpdateCodeListPropertiesRepositoryResponse {

    private final String codeListManifestId;

    public UpdateCodeListPropertiesRepositoryResponse(String codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }
}
