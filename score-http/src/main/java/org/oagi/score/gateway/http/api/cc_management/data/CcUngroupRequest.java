package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CcUngroupRequest {

    private String accManifestId;

    private String asccManifestId;

    private int pos = -1;
}
