package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class UpliftBieRequest {

    private ScoreUser requester;

    private BigInteger topLevelAsbiepId;

    private BigInteger targetAsccpManifestId;

    private List<BigInteger> bizCtxIds;

    private Map<String, String> customMappingTable = Collections.emptyMap();

}
