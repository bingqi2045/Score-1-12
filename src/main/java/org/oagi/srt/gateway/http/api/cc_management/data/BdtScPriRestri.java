package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class BdtScPriRestri {

    private long bdtScPriRestriId;
    private long bdtScId;
    private Long cdtScAwdPriXpsTypeMapId;
    private Long codeListId;
    private Long agencyIdListId;
    private boolean isDefault;

}
