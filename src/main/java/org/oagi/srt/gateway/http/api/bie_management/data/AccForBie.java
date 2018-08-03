package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccForBie extends Trackable {

    private long accId;
    private String guid;

}
