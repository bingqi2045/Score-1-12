package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCC implements CoreComponent {

    private BigInteger bccManifestId = BigInteger.ZERO;
    private BigInteger fromAccManifestId = BigInteger.ZERO;
    private BigInteger bccId = BigInteger.ZERO;
    private String guid;
    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private int entityType;
    private BigInteger fromAccId = BigInteger.ZERO;
    private BigInteger toBccpId = BigInteger.ZERO;
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

    public BigInteger getId() {
        return getBccId();
    }

}
