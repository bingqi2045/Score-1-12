package org.oagi.score.repo.component.code_list;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AvailableCodeList {

    private BigInteger codeListId;
    private BigInteger codeListManifestId;
    private BigInteger basedCodeListManifestId;
    private boolean isDefault;
    private String codeListName;

}
