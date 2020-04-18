package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCC implements CoreComponent {

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
    private Long revisionId;
    private Long releaseId;
    private CcState state;
    private boolean deprecated;
    private boolean nillable;

    public long getId() {
        return getBccId();
    }

}
