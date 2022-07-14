package org.oagi.score.gateway.http.api.business_term_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.PageRequest;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class AssignedBusinessTermListRequest {
    private BigInteger assignedBtId;
    private String businessContext;
    private BigInteger bieId;
    private BigInteger releaseId;
    private List<String> bieTypes;
    private String bieDen;
    private String isPrimary;
    private String typeCode;
    private String businessTerm;
    private String externalReferenceUri;
    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;


}