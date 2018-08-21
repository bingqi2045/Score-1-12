package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class BCC extends Trackable implements CoreComponent {

    private long bccId;
    private String guid;

    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private int entityType;

    private long fromAccId;
    private long toBccpId;

    private String den;
    private String definition;
    private String definitionSource;

    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

    private int state;
    private Long currentBccId;
    private boolean deprecated;
    private boolean nillable;

}
