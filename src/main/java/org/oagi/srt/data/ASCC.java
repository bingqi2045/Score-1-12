package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCC implements CoreComponent {

    private long asccId;
    private String guid;
    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private long fromAccId;
    private long toAsccpId;
    private String den;
    private String definition;
    private String definitionSource;
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
    private boolean deprecated;

    public long getId() {
        return getAsccId();
    }


}
