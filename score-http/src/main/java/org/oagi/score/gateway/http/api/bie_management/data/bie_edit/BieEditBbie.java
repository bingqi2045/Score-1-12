package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import org.oagi.score.data.Cardinality;

@Data
public class BieEditBbie implements Cardinality {

    private String bbieId;
    private String fromAbieId;
    private String toBbiepId;
    private String basedBccManifestId;
    private boolean used;

    private int cardinalityMin;
    private int cardinalityMax;

}
