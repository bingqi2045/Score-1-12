package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BusinessTerm {

    private BigInteger bieBiztermId = BigInteger.ZERO;
    private BigInteger bieId = BigInteger.ZERO;
    private String primaryIndicator;
    private String typeCode;
    private String businessTerm;
    private String guid;
    private String externalReferenceUri;

}
