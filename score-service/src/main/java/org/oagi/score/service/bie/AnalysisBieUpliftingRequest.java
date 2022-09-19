package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;

@Data
public class AnalysisBieUpliftingRequest {

    private ScoreUser requester;

    private String topLevelAsbiepId;

    private String targetReleaseId;

}
