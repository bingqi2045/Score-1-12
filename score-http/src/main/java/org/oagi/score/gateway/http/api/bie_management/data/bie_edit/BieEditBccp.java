package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.service.common.data.TrackableImpl;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBccp extends TrackableImpl<String> {

    private String bccpManifestId;
    private String guid;
    private String propertyTerm;
    private String bdtManifestId;

    @Override
    public String getId() {
        return bccpManifestId;
    }

}
