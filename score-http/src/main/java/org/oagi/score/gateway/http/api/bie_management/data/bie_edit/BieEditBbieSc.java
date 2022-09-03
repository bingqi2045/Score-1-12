package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditBbieSc {

    private String bbieScId;
    private String bbieId;
    private BigInteger dtScManifestId = BigInteger.ZERO;
    private boolean used;

}
