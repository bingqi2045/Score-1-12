package org.oagi.score.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class BusinessContextValue implements Serializable {

    private BigInteger bizCtxValueId;
    private String guid;
    private BigInteger ctxCategoryId;
    private String ctxCategoryName;
    private BigInteger ctxSchemeId;
    private String ctxSchemeName;
    private BigInteger ctxSchemeValueId;
    private String ctxSchemeValue;
    private String ctxSchemeValueMeaning;
    private BigInteger bizCtxId;
}
