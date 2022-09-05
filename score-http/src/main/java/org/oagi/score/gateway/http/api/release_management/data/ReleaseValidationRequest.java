package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class ReleaseValidationRequest {

    private List<String> assignedAccComponentManifestIds = Collections.emptyList();
    private List<String> assignedAsccpComponentManifestIds = Collections.emptyList();
    private List<String> assignedBccpComponentManifestIds = Collections.emptyList();
    private List<String> assignedCodeListComponentManifestIds = Collections.emptyList();
    private List<String> assignedAgencyIdListComponentManifestIds = Collections.emptyList();
    private List<String> assignedDtComponentManifestIds = Collections.emptyList();
}
