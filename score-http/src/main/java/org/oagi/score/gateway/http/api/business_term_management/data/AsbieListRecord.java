package org.oagi.score.gateway.http.api.business_term_management.data;

import lombok.Data;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.repo.api.businesscontext.model.BusinessContext;
import org.oagi.score.service.common.data.AccessPrivilege;

import java.util.List;

@Data
public class AsbieListRecord {
    private String type;
    private String bieId;
    private String guid;
    private String den;
    private BieState state;
    private String version;
    private String status;
    private String bizCtxName;
    private String releaseId;
    private String releaseNum;
    private String remark;
    private String lastUpdateUser;
    private String lastUpdateTimestamp;
    private String used;
    private String topLevelAsbiepId;
    private String topLevelAsccpPropertyTerm;
    private List<BusinessContext> businessContexts;
    private String owner;
    private String ownerUserId;
    private AccessPrivilege access;
}
