package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CreateCodeListRepositoryRequest extends RepositoryRequest {

    private final BigInteger releaseId;

    private String initialName = "Code List";

    public CreateCodeListRepositoryRequest(User user,
                                           BigInteger releaseId) {
        super(user);
        this.releaseId = releaseId;
    }

    public CreateCodeListRepositoryRequest(User user,
                                           LocalDateTime localDateTime,
                                           BigInteger releaseId) {
        super(user, localDateTime);
        this.releaseId = releaseId;
    }

    public BigInteger getReleaseId() {
        return releaseId;
    }

    public String getInitialName() {
        return initialName;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }
}
