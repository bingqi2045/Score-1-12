package org.oagi.score.repo.component.agency_id_list;

import lombok.Data;

@Data
public class AvailableAgencyIdList {

    private String agencyIdListId;
    private String agencyIdListManifestId;
    private String basedAgencyIdListManifestId;
    private String agencyIdListName;
    private String versionId;
    private String state;
    private boolean isDeprecated;
}
