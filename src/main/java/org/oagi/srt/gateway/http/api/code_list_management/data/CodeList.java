package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class CodeList {

    private long codeListId;
    private String codeListName;
    private Long basedCodeListId;
    private String basedCodeListName;
    private long agencyId;
    private String agencyIdName;
    private String versionId;
    private Date lastUpdateTimestamp;
    private boolean extensible;
    private String state;

}
