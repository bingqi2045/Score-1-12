package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.List;

@Data
public class CcUpdateStateListRequest {
    private String action;
    private String toState;
    private List<String> accManifestIds;
    private List<String> bccpManifestIds;
    private List<String> asccpManifestIds;
    private List<String> dtManifestIds;
}
