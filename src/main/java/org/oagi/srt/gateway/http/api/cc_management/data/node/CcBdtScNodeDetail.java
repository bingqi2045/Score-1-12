package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcBdtScNodeDetail implements CcNodeDetail {

    private String type = "bdt_sc";
    private BigInteger manifestId = BigInteger.ZERO;
    private BigInteger bdtScId = BigInteger.ZERO;
    private String guid;
    private String den;
    private int cardinalityMin;
    private int cardinalityMax;
    private String definition;
    private String definitionSource;
    private String defaultValue;
    private String fixedValue;
}
