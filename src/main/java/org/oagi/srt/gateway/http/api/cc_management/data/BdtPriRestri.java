package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

@Data
public class BdtPriRestri {

    private long bdtPriRestriId;
    private long bdtId;
    private Long cdtAwdPriXpsTypeMapId;
    private Long codeListId;
    private Long agencyIdListId;
    private boolean isDefault;

}
