package org.oagi.score.gateway.http.api.namespace_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class NamespaceList {

    private String namespaceId;
    private String uri;
    private String prefix;
    private String owner;
    private String description;
    private boolean isStd;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
}
