package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.service.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAcc extends TrackableImpl<String> {

    private String accManifestId;
    private String guid;
    private int oagisComponentType;
    private String basedAccManifestId;

    @Override
    public String getId() {
        return accManifestId;
    }
}
