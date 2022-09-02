package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CodeListValue {

    private BigInteger codeListValueId = BigInteger.ZERO;
    private String codeListId;
    private String value;
    private String meaning;
    private String definition;
    private String definitionSource;
    private boolean usedIndicator;
    private boolean lockedIndicator;
    private boolean extensionIndicator;
}
