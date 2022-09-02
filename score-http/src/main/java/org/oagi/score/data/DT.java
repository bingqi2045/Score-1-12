package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DT implements CoreComponent {

    private String dtId;
    private String guid;
    private int type;
    private String dataTypeTerm;
    private String representationTerm;
    private String qualifier;
    private String sixDigitId;
    private String basedDtId;
    private String den;
    private String definition;
    private String definitionSource;
    private String contentComponentDefinition;
    private CcState state;
    private String releaseId;
    private String releaseNum;
    private String logId;
    private int revisionNum;
    private int revisionTrackingNum;
    private String createdBy;
    private String ownerUserId;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private boolean deprecated;

    @Override
    public String getId() {
        return getDtId();
    }

}
