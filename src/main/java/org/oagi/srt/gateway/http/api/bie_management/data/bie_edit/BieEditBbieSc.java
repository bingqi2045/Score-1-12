package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditBbieSc {

    private BigInteger bbieScId = BigInteger.ZERO;
    private BigInteger bbieId = BigInteger.ZERO;
    private BigInteger dtScId = BigInteger.ZERO;
    private boolean used;

}
