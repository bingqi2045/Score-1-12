package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.data.Cardinality;
import org.oagi.score.data.SeqKeySupportable;
import org.oagi.score.service.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAscc extends TrackableImpl<String> implements SeqKeySupportable, Cardinality {

    private String asccId;
    private String asccManifestId;
    private String guid;
    private String fromAccManifestId;
    private String toAsccpManifestId;
    private int seqKey;

    private int cardinalityMin;
    private int cardinalityMax;

    @Override
    public String getId() {
        return asccId;
    }

}
