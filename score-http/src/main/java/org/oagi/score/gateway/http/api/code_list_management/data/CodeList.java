package org.oagi.score.gateway.http.api.code_list_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.AccessPrivilege;

import java.util.Collections;
import java.util.List;

@Data
public class CodeList {

    private String codeListManifestId;
    private String codeListName;
    private String guid;
    private String basedCodeListManifestId;
    private String basedCodeListName;
    private String namespaceId;
    private String namespaceUri;

    private String agencyIdListValueManifestId;
    private String agencyIdListValueValue;
    private String agencyIdListValueName;
    private String versionId;

    private String listId;
    private String definition;
    private String definitionSource;
    private String remark;

    private boolean extensible;
    private boolean deprecated;
    private String ownerId;
    private String state;
    private AccessPrivilege access;

    private String owner;
    private String releaseId;
    private String releaseState;
    private String releaseNum;
    private String logId;
    private int revisionNum;
    private int revisionTrackingNum;

    private List<CodeListValue> codeListValues = Collections.emptyList();

}
