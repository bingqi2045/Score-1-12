package org.oagi.score.gateway.http.api.permission_management.data;


import lombok.Data;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

@Data
public class PermissionListRequest {
    private String segment;
    private String object;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;
}
