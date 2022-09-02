package org.oagi.score.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class ContextSchemeValue implements Serializable {

    private String ctxSchemeValueId;
    private String guid;
    private String value;
    private String meaning;
    private String ownerCtxSchemeId;
}
