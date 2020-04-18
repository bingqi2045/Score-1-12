package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCCP implements CoreComponent {

    private long asccpId;
    private String guid;
    private String propertyTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private Long roleOfAccId;
    private Long moduleId;
    private String module;
    private Long namespaceId;
    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private CcState state;
    private long releaseId;
    private String releaseNum;
    private long revisionId;
    private int revisionNum;
    private int revisionTrackingNum;
    private boolean reusableIndicator;
    private boolean deprecated;
    private boolean nillable;

    public long getId() {
        return getAsccpId();
    }

}
