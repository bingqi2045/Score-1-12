package org.oagi.srt.repo.component.module;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class DeleteModuleSetRepositoryRequest extends RepositoryRequest {

    private final BigInteger moduleSetId;

    public DeleteModuleSetRepositoryRequest(User user,
                                            BigInteger moduleSetId) {
        super(user);
        this.moduleSetId = moduleSetId;
    }

    public DeleteModuleSetRepositoryRequest(User user,
                                            LocalDateTime localDateTime,
                                            BigInteger moduleSetId) {
        super(user, localDateTime);
        this.moduleSetId = moduleSetId;
    }

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }
}
