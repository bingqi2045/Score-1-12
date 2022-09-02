package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SimpleAgencyIdListValue {

    private BigInteger agencyIdListValueManifestId;
    private BigInteger agencyIdListManifestId;
    private String agencyIdListValueId;
    private String value;
    private String name;

}
