package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.Cardinality;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAscc extends TrackableImpl implements SeqKeySupportable, Cardinality {

    private long asccId;
    private String guid;
    private long fromAccManifestId;
    private long toAsccpManifestId;
    private int seqKey;

    private int cardinalityMin;
    private int cardinalityMax;

    @Override
    public long getId() {
        return asccId;
    }

}
