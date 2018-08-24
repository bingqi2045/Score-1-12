package org.oagi.srt.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class BdtPriRestri implements Serializable {

    private long bdtPriRestriId;
    private long bdtId;
    private Long cdtAwdPriXpsTypeMapId;
    private Long codeListId;
    private Long agencyIdListId;
    private boolean defaulted;

}
