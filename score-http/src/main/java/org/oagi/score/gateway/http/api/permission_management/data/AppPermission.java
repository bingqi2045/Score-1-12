package org.oagi.score.gateway.http.api.permission_management.data;

import lombok.Data;
import org.jooq.types.ULong;

@Data
public class AppPermission {
    private ULong appPermissionId;
    private String segment;
    private String object;
    private String operation;
    private String description;
}
