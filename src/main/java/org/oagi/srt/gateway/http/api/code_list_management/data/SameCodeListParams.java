package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

@Data
public class SameCodeListParams {

    private long releaseId;
    private Long codeListManifestId;
    private String listId;
    private long agencyId;
    private String versionId;
}
