package org.oagi.srt.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CodeListValue {

    private BigInteger codeListValueId = BigInteger.ZERO;
    private BigInteger codeListId = BigInteger.ZERO;
    private String value;
    private String name;
    private String definition;
    private String definitionSource;
    private boolean usedIndicator;
    private boolean lockedIndicator;
    private boolean extensionIndicator;
}
