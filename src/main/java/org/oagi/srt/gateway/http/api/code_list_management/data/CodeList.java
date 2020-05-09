package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class CodeList {

    private BigInteger releaseId;
    private BigInteger codeListManifestId;
    private String codeListName;
    private String guid;
    private Long basedCodeListManifestId;
    private String basedCodeListName;

    private BigInteger agencyId;
    private String agencyIdName;
    private String versionId;

    private String listId;
    private String definition;
    private String definitionSource;
    private String remark;

    private boolean extensible;
    private boolean deprecated;
    private String state;

    private List<CodeListValue> codeListValues = Collections.emptyList();

}
