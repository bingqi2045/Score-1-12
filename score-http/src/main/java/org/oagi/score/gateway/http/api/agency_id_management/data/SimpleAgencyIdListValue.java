package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SimpleAgencyIdListValue {

    private String agencyIdListValueManifestId;
    private String agencyIdListManifestId;
    private String agencyIdListValueId;
    private String value;
    private String name;

}
