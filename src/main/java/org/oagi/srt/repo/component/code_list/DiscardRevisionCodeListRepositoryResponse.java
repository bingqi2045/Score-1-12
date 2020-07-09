package org.oagi.srt.repo.component.code_list;

import java.math.BigInteger;

public class DiscardRevisionCodeListRepositoryResponse {

    private final BigInteger codeListManifestId;

    public DiscardRevisionCodeListRepositoryResponse(BigInteger codeListManifestId) {
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }
}
