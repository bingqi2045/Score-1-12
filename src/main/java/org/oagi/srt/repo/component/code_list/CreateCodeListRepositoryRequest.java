package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateCodeListRepositoryRequest extends RepositoryRequest {

    private final BigInteger basedCodeListManifestId;
    private final BigInteger releaseId;

    private String initialName = "Code List";

    public CreateCodeListRepositoryRequest(User user,
                                           LocalDateTime localDateTime,
                                           BigInteger basedCodeListManifestId,
                                           BigInteger releaseId) {
        super(user, localDateTime);
        this.basedCodeListManifestId = basedCodeListManifestId;
        this.releaseId = releaseId;
    }

    public String getInitialName() {
        return initialName;
    }

    public BigInteger getbasedCodeListManifestId() {
        return this.basedCodeListManifestId;
    }

    public BigInteger getReleaseId() {
        return this.releaseId;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }
}
