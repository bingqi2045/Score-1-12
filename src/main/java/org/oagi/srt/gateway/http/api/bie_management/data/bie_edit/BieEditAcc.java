package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditAcc extends TrackableImpl {

    private long accId;
    private String guid;
    private int oagisComponentType;
    private Long basedAccId;

    @Override
    public long getId() {
        return accId;
    }
}
