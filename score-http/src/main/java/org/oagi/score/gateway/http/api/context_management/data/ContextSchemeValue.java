package org.oagi.score.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class ContextSchemeValue implements Serializable {

    private BigInteger ctxSchemeValueId;
    private String guid;
    private String value;
    private String meaning;
    private BigInteger ownerCtxSchemeId;
    boolean used;

}
