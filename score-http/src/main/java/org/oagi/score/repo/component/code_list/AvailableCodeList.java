package org.oagi.score.repo.component.code_list;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode
public class AvailableCodeList {

    private String codeListId;
    private String codeListManifestId;
    private String basedCodeListManifestId;
    private String codeListName;
    private String state;
    private String versionId;
    private boolean isDeprecated;

}
