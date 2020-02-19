package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;
import org.jooq.types.ULong;

import java.util.List;

@Data
public class BieCreateRequest {

    private long asccpManifestId;
    private List<Long> bizCtxIds;

    public ULong asccpManifestId() {
        return ULong.valueOf(asccpManifestId);
    }

}
