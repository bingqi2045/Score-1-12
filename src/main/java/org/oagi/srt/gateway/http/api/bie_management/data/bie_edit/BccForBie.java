package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class BccForBie extends TrackableImpl {

    private BigInteger bccId;
    private String guid;
    private String cardinalityMin;
    private String cardinalityMax;
    private String den;
    private String definition;
    private String definitionSource;
    private String toBccpId;
    private String fromAccId;

    @Override
    public BigInteger getId() {
        return bccId;
    }

}
