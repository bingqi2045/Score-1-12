package org.oagi.srt.gateway.http.api.account_management.data;

import lombok.Data;

@Data
public class UpdatePasswordRequest {

    private String oldPassword;
    private String newPassword;

}
