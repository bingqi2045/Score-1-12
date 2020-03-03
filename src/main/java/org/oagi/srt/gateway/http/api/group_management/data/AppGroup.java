package org.oagi.srt.gateway.http.api.group_management.data;

import lombok.Data;
import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.account_management.data.AppUser;
import org.oagi.srt.gateway.http.api.permission_management.data.AppPermission;

import java.util.List;

@Data
public class AppGroup {
    private ULong appGroupId;
    private String name;
    private Integer authority;
    private List<AppPermission> groupPermissions;
    private List<AppUser> groupUsers;
}
