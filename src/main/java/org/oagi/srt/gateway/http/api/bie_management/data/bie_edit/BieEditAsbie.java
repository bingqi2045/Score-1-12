package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAsbie extends Cardinality {

    private long asbieId;
    private long fromAbieId;
    private long toAsbiepId;
    private long basedAsccId;
    private boolean used;

}
