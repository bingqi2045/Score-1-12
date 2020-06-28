package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ASCCP implements CoreComponent {

    private BigInteger asccpManifestId = BigInteger.ZERO;
    private BigInteger asccpId = BigInteger.ZERO;
    private String guid;
    private String propertyTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private BigInteger roleOfAccId = BigInteger.ZERO;
    private BigInteger namespaceId = BigInteger.ZERO;
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
    private boolean reusableIndicator;
    private boolean deprecated;
    private boolean nillable;

    public BigInteger getId() {
        return getAsccpId();
    }

}
