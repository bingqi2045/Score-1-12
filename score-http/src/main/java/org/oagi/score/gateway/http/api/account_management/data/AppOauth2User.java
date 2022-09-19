package org.oagi.score.gateway.http.api.account_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class AppOauth2User {

    private String appOauth2UserId;
    private String appUserId;
    private String providerName;
    private String name;
    private String email;
    private String sub;
    private String nickname;
    private String preferredUsername;
    private String phoneNumber;
    private Date creationTimestamp;

}