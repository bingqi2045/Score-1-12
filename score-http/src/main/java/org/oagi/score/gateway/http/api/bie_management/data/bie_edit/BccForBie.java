package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.service.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BccForBie extends TrackableImpl<String> {

    private String bccId;
    private String guid;
    private String cardinalityMin;
    private String cardinalityMax;
    private String den;
    private String definition;
    private String definitionSource;
    private String toBccpId;
    private String fromAccId;

    @Override
    public String getId() {
        return bccId;
    }

}
