package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class CreateOagisVerbRequest {

    private String basedVerbAccManifestId;

    private String initialObjectClassTerm;

    private String initialPrpertyTerm;

}
