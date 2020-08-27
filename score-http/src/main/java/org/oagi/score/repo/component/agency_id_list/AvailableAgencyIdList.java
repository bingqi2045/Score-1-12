package org.oagi.score.repo.component.agency_id_list;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AvailableAgencyIdList {

    private BigInteger agencyIdListId;
    private boolean isDefault;
    private String agencyIdListName;

}
