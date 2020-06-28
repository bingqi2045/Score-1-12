package org.oagi.srt.repo.component.module;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateModuleSetRepositoryRequest extends RepositoryRequest {

    private final BigInteger moduleSetId;

    private String name;
    private String description;

    public UpdateModuleSetRepositoryRequest(User user,
                                            BigInteger moduleSetId) {
        super(user);
        this.moduleSetId = moduleSetId;
    }

    public UpdateModuleSetRepositoryRequest(User user,
                                            LocalDateTime localDateTime,
                                            BigInteger moduleSetId) {
        super(user, localDateTime);
        this.moduleSetId = moduleSetId;
    }

    public BigInteger getModuleSetId() {
        return moduleSetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
