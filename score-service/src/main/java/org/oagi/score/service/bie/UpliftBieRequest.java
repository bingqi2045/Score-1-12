package org.oagi.score.service.bie;

import lombok.Data;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.bie.model.BieUpliftingCustomMappingTable;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class UpliftBieRequest {

    private ScoreUser requester;

    private BigInteger topLevelAsbiepId;

    private BigInteger targetAsccpManifestId;

    private List<BieUpliftingCustomMappingTable.BieUpliftingMapping> customMappingTable = Collections.emptyList();

}
