package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.bie.model.BieUpliftingMapping;

import java.math.BigInteger;
import java.util.List;

@Data
public class UpliftValidationRequest {
    private ScoreUser requester;
    private String topLevelAsbiepId;
    private String targetReleaseId;
    private List<BieUpliftingMapping> mappingList;
    private String targetAsccpManifestId;
}
