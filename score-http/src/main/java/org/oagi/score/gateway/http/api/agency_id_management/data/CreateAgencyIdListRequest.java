package org.oagi.score.gateway.http.api.agency_id_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CreateAgencyIdListRequest {
    private String releaseId;
    private String basedAgencyIdListManifestId;
}