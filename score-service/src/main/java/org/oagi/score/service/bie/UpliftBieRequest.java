package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.bie.model.BieUpliftingMapping;

import java.util.Collections;
import java.util.List;

@Data
public class UpliftBieRequest {

    private ScoreUser requester;

    private String topLevelAsbiepId;

    private String targetAsccpManifestId;

    private List<BieUpliftingMapping> customMappingTable = Collections.emptyList();

}
