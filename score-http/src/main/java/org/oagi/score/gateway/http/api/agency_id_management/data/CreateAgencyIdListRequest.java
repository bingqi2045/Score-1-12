package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

@Data
public class CreateAgencyIdListRequest {
    private String releaseId;
    private String basedAgencyIdListManifestId;
}