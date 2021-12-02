package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CcBdtPriRestri {

    private BigInteger bdtPriRestriId;
    private PrimitiveRestriType type;
    private BigInteger cdtAwdPriXpsTypeMapId;
    private BigInteger cdtAwdPriId;
    private String primitiveName;
    private BigInteger codeListId;
    private String codeListName;
    private BigInteger agencyIdListId;
    private String agencyIdListName;
    private boolean isDefault;
    private List<CcXbt> xbtList;
    private boolean inherited;
}
