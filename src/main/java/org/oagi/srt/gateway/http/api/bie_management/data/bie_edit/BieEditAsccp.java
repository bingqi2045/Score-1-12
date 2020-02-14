package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsccp extends TrackableImpl {

    private long asccpId;
    private String guid;
    private String propertyTerm;
    private long roleOfAccManifestId;

    @Override
    public long getId() {
        return asccpId;
    }

}
