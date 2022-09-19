package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

import java.util.List;

@Data
public class UpdateAgencyIdListListRequest {
    public List<String> agencyIdListManifestIds;
}
