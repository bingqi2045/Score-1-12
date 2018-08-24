package org.oagi.srt.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class BdtScPriRestri implements Serializable {

    private long bdtScPriRestriId;
    private long bdtScId;
    private Long cdtScAwdPriXpsTypeMapId;
    private Long codeListId;
    private Long agencyIdListId;
    private boolean defaulted;

}
