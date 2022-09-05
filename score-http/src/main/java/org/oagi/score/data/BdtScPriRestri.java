package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BdtScPriRestri implements Serializable {

    private String bdtScPriRestriId;
    private String bdtScId;
    private String cdtScAwdPriXpsTypeMapId;
    private String codeListId;
    private String agencyIdListId;
    private boolean defaulted;

    public boolean isDefault() {
        return isDefaulted();
    }

}
