package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class CodeListForList {

    private long codeListManifestId;
    private String codeListName;
    private String guid;
    private Long basedCodeListManifestId;
    private String basedCodeListName;
    private long agencyId;
    private String listId;
    private String agencyIdName;
    private String versionId;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
    private boolean extensible;
    private String state;
    private boolean deprecated;

}
