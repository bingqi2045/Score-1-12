package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

@Data
public class SimpleAgencyIdListValue {

    private String agencyIdListValueManifestId;
    private String agencyIdListManifestId;
    private String agencyIdListValueId;
    private String value;
    private String name;

}
