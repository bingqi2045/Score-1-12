package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

@Data
public class SimpleAgencyIdList {

    private String agencyIdListManifestId;
    private String agencyIdListId;
    private String name;
    private String state;

}
