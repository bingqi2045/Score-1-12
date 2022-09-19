package org.oagi.score.repo.component.dt;

import lombok.Data;

@Data
public class BdtNode {

    private String dataTypeTerm;
    private String qualifier;
    private String definition;
    private String den;
    private String bdtManifestId;

}
