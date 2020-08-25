package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCC implements CoreComponent {

    private BigInteger asccManifestId = BigInteger.ZERO;
    private BigInteger asccId = BigInteger.ZERO;
    private String guid;
    private int cardinalityMin;
    private int cardinalityMax;
    private int seqKey;
    private BigInteger fromAccId = BigInteger.ZERO;
    private BigInteger toAsccpId = BigInteger.ZERO;
    private String den;
    private String definition;
    private String definitionSource;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger ownerUserId = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private CcState state;
    private BigInteger releaseId = BigInteger.ZERO;
    private String releaseNum;
    private BigInteger revisionId = BigInteger.ZERO;
    private int revisionNum;
    private int revisionTrackingNum;
    private boolean deprecated;

    public BigInteger getId() {
        return getAsccId();
    }


}
