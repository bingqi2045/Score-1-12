package org.oagi.score.gateway.http.api.namespace_management.data;

import lombok.Data;

@Data
public class Namespace {

    private String namespaceId;
    private String uri;
    private String prefix;
    private String description;
    private boolean isStd;
    private boolean canEdit;
}
