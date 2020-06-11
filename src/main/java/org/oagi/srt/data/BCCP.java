package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BCCP implements CoreComponent {

    private BigInteger bccpManifestId = BigInteger.ZERO;
    private BigInteger bccpId = BigInteger.ZERO;
    private String guid;
    private String propertyTerm;
    private String representationTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private String defaultValue;
    private BigInteger bdtId = BigInteger.ZERO;
    private BigInteger moduleId = BigInteger.ZERO;
    private String module;
    private BigInteger namespaceId = BigInteger.ZERO;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger ownerUserId = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;;
    private CcState state;
    private BigInteger releaseId = BigInteger.ZERO;
    private String releaseNum;
    private BigInteger revisionId = BigInteger.ZERO;
    private int revisionNum;
    private int revisionTrackingNum;
    private boolean deprecated;
    private boolean nillable;

    public BigInteger getId() {
        return getBccpId();
    }

}
