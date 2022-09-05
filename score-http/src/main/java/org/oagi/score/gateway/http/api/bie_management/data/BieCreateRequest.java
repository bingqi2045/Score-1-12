package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;
import org.jooq.types.ULong;

import java.math.BigInteger;
import java.util.List;

@Data
public class BieCreateRequest {

    private String asccpManifestId;
    private List<String> bizCtxIds;

    public String asccpManifestId() {
        return asccpManifestId;
    }

}
