package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.data.Cardinality;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBcc extends TrackableImpl implements SeqKeySupportable, Cardinality {

    private long bccId;
    private long bccManifestId;
    private String guid;
    private long fromAccManifestId;
    private long toBccpManifestId;
    private int seqKey;
    private int entityType;

    private int cardinalityMin;
    private int cardinalityMax;

    public boolean isAttribute() {
        return (entityType == 0);
    }

    @Override
    public long getId() {
        return bccId;
    }

}
