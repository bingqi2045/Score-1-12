package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class BCCP extends Trackable implements CoreComponent {

    private long bccpId;
    private String guid;

    private String propertyTerm;
    private String representationTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private String defaultValue;

    private long bdtId;
    private Long moduleId;
    private Long namespaceId;

    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

    private int state;
    private Long currentBccpId;
    private boolean deprecated;
    private boolean nillable;

}
