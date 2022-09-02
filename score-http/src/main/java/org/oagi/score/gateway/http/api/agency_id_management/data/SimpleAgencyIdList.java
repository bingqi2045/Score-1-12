package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SimpleAgencyIdList {

    private BigInteger agencyIdListManifestId;
    private String agencyIdListId;
    private String name;
    private String state;

}
