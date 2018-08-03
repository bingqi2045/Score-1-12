package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBcc extends Trackable implements SeqKeySupportable {

    private long bccId;
    private String guid;
    private long fromAccId;
    private long toBccpId;
    private int seqKey;
    private int entityType;
    private long currentBccId;

    public boolean isAttribute() {
        return (entityType == 0);
    }

}
