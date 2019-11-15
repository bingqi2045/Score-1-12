package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBbie extends Cardinality {

    private long bbieId;
    private long fromAbieId;
    private long toBbiepId;
    private long basedBccId;
    private boolean used;

}
