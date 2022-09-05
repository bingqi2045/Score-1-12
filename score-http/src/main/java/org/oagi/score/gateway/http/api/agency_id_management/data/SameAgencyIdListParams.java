package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

@Data
public class SameAgencyIdListParams {

    private String releaseId;
    private String agencyIdListManifestId;
    private String listId;
    private String agencyIdListValueManifestId;
    private String versionId;
}
