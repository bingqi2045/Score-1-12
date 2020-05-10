package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DT implements CoreComponent {

    private BigInteger dtId = BigInteger.ZERO;
    private String guid;
    private int type;
    private String versionNum;
    private BigInteger previousVersionDtId = BigInteger.ZERO;
    private String dataTypeTerm;
    private String qualifier;
    private BigInteger basedDtId = BigInteger.ZERO;
    private String den;
    private String contentComponentDen;
    private String definition;
    private String definitionSource;
    private String contentComponentDefinition;
    private String revisionDoc;
    private CcState state;
    private BigInteger releaseId = BigInteger.ZERO;
    private String releaseNum;
    private BigInteger revisionId = BigInteger.ZERO;
    private int revisionNum;
    private int revisionTrackingNum;
    private BigInteger moduleId = BigInteger.ZERO;
    private String module;
    private BigInteger createdBy = BigInteger.ZERO;
    private BigInteger ownerUserId = BigInteger.ZERO;
    private BigInteger lastUpdatedBy = BigInteger.ZERO;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private boolean deprecated;

    @Override
    public BigInteger getId() {
        return getDtId();
    }

}
