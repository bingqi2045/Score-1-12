package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsccp extends TrackableImpl {

    private BigInteger asccpId;
    private String guid;
    private String propertyTerm;
    private BigInteger roleOfAccManifestId;

    @Override
    public BigInteger getId() {
        return asccpId;
    }

}
