package org.oagi.score.gateway.http.api.code_list_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.AccessPrivilege;

import java.util.Date;

@Data
public class CodeListForList {

    private String codeListManifestId;
    private String codeListId;
    private String codeListName;
    private String definition;
    private String definitionSource;
    private String modulePath;
    private String guid;
    private String basedCodeListManifestId;
    private String basedCodeListName;
    private String agencyIdListValueManifestId;
    private String agencyIdListValueValue;
    private String agencyIdListValueName;
    private String listId;
    private String versionId;
    private boolean extensible;
    private boolean deprecated;
    private String revision;
    private String state;
    private AccessPrivilege access;
    private String ownerId;
    private String owner;
    private String lastUpdateUser;
    private Date lastUpdateTimestamp;

}
