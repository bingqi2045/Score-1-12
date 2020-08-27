package org.oagi.score.gateway.http.api.group_management.data;


import lombok.Data;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

@Data
public class GroupListRequest {
    private String name;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;
}
