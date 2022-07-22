package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class ExportStandaloneSchemaRequest {

    private List<BigInteger> asccpManifestIdList;

}
