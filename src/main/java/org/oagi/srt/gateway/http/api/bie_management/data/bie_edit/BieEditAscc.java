package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.SeqKeySupportable;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAscc extends Trackable implements SeqKeySupportable {

    private long asccId;
    private String guid;
    private long fromAccId;
    private long toAsccpId;
    private int seqKey;
    private long currentAsccId;

}
