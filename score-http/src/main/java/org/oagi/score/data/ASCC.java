package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCC implements CoreComponent {

    private String asccManifestId;
    private String fromAccManifestId;
    private String toAsccpManifestId;
    private String asccId;
    private String guid;
    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private String fromAccId;
    private String toAsccpId;
    private String den;
    private String definition;
    private String definitionSource;
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
    private boolean deprecated;
    private String seqKeyId;
    private String prevSeqKeyId;
    private String nextSeqKeyId;

    public String getId() {
        return getAsccId();
    }


}
