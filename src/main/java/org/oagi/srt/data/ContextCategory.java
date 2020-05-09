package org.oagi.srt.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class ContextCategory implements Serializable {

    private BigInteger ctxCategoryId = BigInteger.ZERO;
    private String guid;
    private String name;
    private String description;
}
