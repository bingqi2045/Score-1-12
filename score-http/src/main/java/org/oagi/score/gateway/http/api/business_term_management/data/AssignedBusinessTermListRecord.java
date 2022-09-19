package org.oagi.score.gateway.http.api.business_term_management.data;

import lombok.Data;
import org.oagi.score.repo.api.businesscontext.model.BusinessContext;

import java.util.List;

@Data
public class AssignedBusinessTermListRecord {
    private List<BusinessContext> businessContexts;
    private String assignedBizTermId;
    private String bieId;
    private String bieType;
    private String den;
    private String primaryIndicator;
    private Boolean isPrimary;
    private String typeCode;
    private String businessTermId;
    private String businessTerm;
    private String externalReferenceUri;
    private String lastUpdatedBy;
    private String owner;
    private String lastUpdateTimestamp;

}
