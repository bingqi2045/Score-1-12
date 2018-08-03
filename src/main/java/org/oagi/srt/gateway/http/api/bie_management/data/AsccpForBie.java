package org.oagi.srt.gateway.http.api.bie_management.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class AsccpForBie extends Trackable {

    private long asccpId;
    private String guid;
    private String propertyTerm;
    private Long moduleId;
    private String module;
    private Date lastUpdateTimestamp;

}
