package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditCodeList {

    private String codeListManifestId;
    private String basedCodeListManifestId;
    private String codeListId;
    private String basedCodeListId;
    private boolean isDefault;
    private String codeListName;

}
