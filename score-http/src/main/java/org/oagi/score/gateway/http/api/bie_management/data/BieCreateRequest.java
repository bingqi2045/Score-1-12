package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;
import org.jooq.types.ULong;

import java.math.BigInteger;
import java.util.List;

@Data
public class BieCreateRequest {

    private BigInteger asccpManifestId;
    private List<String> bizCtxIds;

    public ULong asccpManifestId() {
        return ULong.valueOf(asccpManifestId);
    }

}
