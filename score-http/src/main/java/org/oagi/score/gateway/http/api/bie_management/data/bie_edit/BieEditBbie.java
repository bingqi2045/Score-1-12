package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import org.oagi.score.data.Cardinality;

import java.math.BigInteger;

@Data
public class BieEditBbie implements Cardinality {

    private String bbieId;
    private String fromAbieId;
    private String toBbiepId;
    private BigInteger basedBccManifestId;
    private boolean used;

    private int cardinalityMin;
    private int cardinalityMax;

}
