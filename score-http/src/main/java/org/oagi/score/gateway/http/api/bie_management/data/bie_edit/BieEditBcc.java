package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.data.Cardinality;
import org.oagi.score.data.SeqKeySupportable;
import org.oagi.score.service.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBcc extends TrackableImpl<String> implements SeqKeySupportable, Cardinality {

    private String bccId;
    private String bccManifestId;
    private String guid;
    private String fromAccManifestId;
    private String toBccpManifestId;
    private int seqKey;
    private int entityType;

    private int cardinalityMin;
    private int cardinalityMax;

    public boolean isAttribute() {
        return (entityType == 0);
    }

    @Override
    public String getId() {
        return bccId;
    }

}
