package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.service.common.data.TrackableImpl;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsccp extends TrackableImpl<String> {

    private String asccpManifestId;
    private String guid;
    private String propertyTerm;
    private String roleOfAccManifestId;

    @Override
    public String getId() {
        return asccpManifestId;
    }

}
