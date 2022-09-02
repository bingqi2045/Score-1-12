package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CcBdtPriRestri {

    private BigInteger bdtPriRestriId;
    private PrimitiveRestriType type;
    private String cdtAwdPriId;
    private String primitiveName;

    private String cdtAwdPriXpsTypeMapId;
    private String xbtId;
    private String xbtName;

    private String codeListId;
    private String codeListName;

    private String agencyIdListId;
    private String agencyIdListName;

    private boolean isDefault;

    private boolean inherited;
}
