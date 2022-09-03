package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;

@Data
public class AnalysisBieUpliftingRequest {

    private ScoreUser requester;

    private String topLevelAsbiepId;

    private String targetReleaseId;

}
