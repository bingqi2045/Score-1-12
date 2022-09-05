package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCC implements CoreComponent {

    private String bccManifestId;
    private String fromAccManifestId;
    private String bccId;
    private String guid;
    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private int entityType;
    private String fromAccId;
    private String toBccpId;
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
    private boolean nillable;
    private BigInteger seqKeyId = BigInteger.ZERO;
    private BigInteger prevSeqKeyId = BigInteger.ZERO;
    private BigInteger nextSeqKeyId = BigInteger.ZERO;

    public String getId() {
        return getBccId();
    }

}
