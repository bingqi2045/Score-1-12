package org.oagi.score.service.common.data;

import lombok.Data;

@Data
public class AppUser {

    private String appUserId;
    private String loginId;
    private String password;
    private String name;
    private String organization;
    private boolean developer;
    private boolean admin;
    private boolean enabled;

}
