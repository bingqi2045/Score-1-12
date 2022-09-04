package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CcBdtScPriRestri {

    private String bdtScPriRestriId;
    private PrimitiveRestriType type;
    private String cdtScAwdPriId;
    private String primitiveName;

    private String cdtScAwdPriXpsTypeMapId;
    private String xbtId;
    private String xbtName;

    private String codeListId;
    private String codeListName;

    private String agencyIdListId;
    private String agencyIdListName;

    private boolean isDefault;

    private boolean inherited;
}
