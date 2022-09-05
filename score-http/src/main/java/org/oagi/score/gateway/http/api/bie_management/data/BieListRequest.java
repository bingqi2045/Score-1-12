package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.service.common.data.AccessPrivilege;
import org.oagi.score.service.common.data.PageRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class BieListRequest {

    private String den;
    private String propertyTerm;
    private String businessContext;
    private String asccpManifestId;
    private String usageTopLevelAsbiepId;
    private AccessPrivilege access;
    private List<String> excludePropertyTerms = Collections.emptyList();
    private List<String> excludeTopLevelAsbiepIds = Collections.emptyList();
    private List<BieState> states = Collections.emptyList();
    private List<String> types = Collections.emptyList();
    private List<String> ownerLoginIds = Collections.emptyList();
    private List<String> updaterLoginIds = Collections.emptyList();
    private String releaseId;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;
    private Boolean ownedByDeveloper;

    private String asccBccDen;
    private String bieId;
}
