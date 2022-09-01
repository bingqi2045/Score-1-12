package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCCP implements CoreComponent {

    private BigInteger asccpManifestId = BigInteger.ZERO;
    private BigInteger roleOfAccManifestId = BigInteger.ZERO;
    private BigInteger asccpId = BigInteger.ZERO;
    private String guid;
    private String propertyTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private BigInteger roleOfAccId = BigInteger.ZERO;
    private BigInteger namespaceId = BigInteger.ZERO;
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

    public BigInteger getId() {
        return getAsccpId();
    }

}
