package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BdtPriRestri implements Serializable {

    private String bdtPriRestriId;
    private String bdtId;
    private String cdtAwdPriXpsTypeMapId;
    private String codeListId;
    private String agencyIdListId;
    private boolean defaulted;

    public boolean isDefault() {
        return isDefaulted();
    }

}
