package org.oagi.score.gateway.http.api.code_list_management.data;

import lombok.Data;

@Data
public class SameCodeListParams {

    private String releaseId;
    private Long codeListManifestId;
    private String listId;
    private long agencyIdListValueManifestId;
    private String versionId;
}
