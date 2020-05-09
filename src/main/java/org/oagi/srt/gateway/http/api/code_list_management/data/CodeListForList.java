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
    private boolean extensible;
    private boolean deprecated;
    private String revision;
    private String state;
    private String owner;
    private String lastUpdateUser;
    private Date lastUpdateTimestamp;

}
