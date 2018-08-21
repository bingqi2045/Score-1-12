package org.oagi.srt.gateway.http.api.cc_management.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCCP implements CoreComponent {

    private long bccpId;
    public long getId() {
        return getBccpId();
    }

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

    private int revisionNum;
    private int revisionTrackingNum;
    private Integer revisionAction;
    private Long releaseId;

    private int state;
    private Long currentBccpId;
    private boolean deprecated;
    private boolean nillable;

}
