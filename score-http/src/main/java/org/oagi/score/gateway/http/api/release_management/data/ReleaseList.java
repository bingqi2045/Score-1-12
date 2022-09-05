package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class ReleaseList {

    private String releaseId;
    private String guid;
    private String releaseNum;
    private String releaseNote;
    private ReleaseState state;

    private String createdBy;
    private Date creationTimestamp;

    private String lastUpdatedBy;
    private Date lastUpdateTimestamp;

}
