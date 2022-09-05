package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import org.oagi.score.data.Cardinality;

@Data
public class BieEditAsbie implements Cardinality {

    private String asbieId;
    private String fromAbieId;
    private String toAsbiepId;
    private String basedAsccManifestId;
    private boolean used;

    private int cardinalityMin;
    private int cardinalityMax;

}
