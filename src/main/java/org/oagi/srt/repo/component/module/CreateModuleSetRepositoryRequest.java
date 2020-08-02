package org.oagi.srt.repo.component.module;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

public class CreateModuleSetRepositoryRequest extends RepositoryRequest {

    private String name;
    private String description;

    public CreateModuleSetRepositoryRequest(User user) {
        super(user);
    }

    public CreateModuleSetRepositoryRequest(User user,
                                            LocalDateTime localDateTime) {
        super(user, localDateTime);
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