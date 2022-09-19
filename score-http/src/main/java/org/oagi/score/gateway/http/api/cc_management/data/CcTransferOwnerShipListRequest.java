package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.List;

@Data
public class CcTransferOwnerShipListRequest {
    private String targetLoginId;
    private List<String> accManifestIds;
    private List<String> bccpManifestIds;
    private List<String> asccpManifestIds;
    private List<String> dtManifestIds;
}
