package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CcTag {

    private BigInteger ccTagId;

    private String tagName;

}
