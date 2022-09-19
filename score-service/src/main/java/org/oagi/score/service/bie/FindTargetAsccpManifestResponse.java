package org.oagi.score.service.bie;

import lombok.Data;

@Data
public class FindTargetAsccpManifestResponse {

    private String asccpManifestId;

    private BieDocument bieDocument;

    private String releaseNum;

}
