package org.oagi.score.data;

import lombok.Data;
import org.oagi.score.repo.api.bie.model.BieState;

import java.util.Date;

@Data
public class TopLevelAsbiep {

    private String topLevelAsbiepId;
    private String asbiepId;
    private String ownerUserId;
    private String releaseId;
    private String version;
    private String status;
    private BieState state;
    private String lastUpdatedBy;
    private Date lastUpdateTimestamp;

}
