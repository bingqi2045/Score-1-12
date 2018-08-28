package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCCP implements CoreComponent {

    private long asccpId;
    public long getId() {
        return getAsccpId();
    }

    private String guid;

    private String propertyTerm;
    private String den;
    private String definition;
    private String definitionSource;

    private Long roleOfAccId;
    private Long moduleId;
    private Long namespaceId;

    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

    private int revisionNum;
    private int revisionTrackingNum;
    private Integer revisionAction;
    private Long releaseId;

    private int state;
    private Long currentAsccpId;
    private boolean reusableIndicator;
    private boolean deprecated;
    private boolean nillable;

}
