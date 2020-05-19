package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;
import org.oagi.srt.entity.jooq.enums.ReleaseState;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ReleaseList {

    private BigInteger releaseId;
    private String releaseNum;
    private ReleaseState state;

    private String createdBy;
    private Date creationTimestamp;

    private String lastUpdatedBy;
    private Date lastUpdateTimestamp;

}
