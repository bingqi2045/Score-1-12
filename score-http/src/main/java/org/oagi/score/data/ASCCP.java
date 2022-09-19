package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCCP implements CoreComponent {

    private String asccpManifestId;
    private String roleOfAccManifestId;
    private String asccpId;
    private String guid;
    private String propertyTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private String roleOfAccId;
    private String namespaceId;
    private String createdBy;
    private String ownerUserId;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private CcState state;
    private String releaseId;
    private String releaseNum;
    private String logId;
    private int revisionNum;
    private int revisionTrackingNum;
    private boolean reusableIndicator;
    private boolean deprecated;
    private boolean nillable;

    public String getId() {
        return getAsccpId();
    }

}
