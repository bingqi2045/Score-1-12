package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBccp extends Trackable {

    private long bccpId;
    private String guid;
    private String propertyTerm;
    private long bdtId;
    private long currentBccpId;

}
