package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BusinessTerm {

    private String bieBiztermId;
    private String bieId;
    private String primaryIndicator;
    private String typeCode;
    private String businessTerm;
    private String guid;
    private String externalReferenceUri;

}
