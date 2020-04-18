package org.oagi.srt.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DT implements CoreComponent {

    private long dtId;
    private String guid;
    private int type;
    private String versionNum;
    private Long previousVersionDtId;
    private String dataTypeTerm;
    private String qualifier;
    private Long basedDtId;
    private String den;
    private String contentComponentDen;
    private String definition;
    private String definitionSource;
    private String contentComponentDefinition;
    private String revisionDoc;
    private CcState state;
    private Long moduleId;
    private String module;
    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private long revisionId;
    private Long releaseId;
    private boolean deprecated;

    @Override
    public long getId() {
        return getDtId();
    }

}
