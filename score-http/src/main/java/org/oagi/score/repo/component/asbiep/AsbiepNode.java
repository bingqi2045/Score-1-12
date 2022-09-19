package org.oagi.score.repo.component.asbiep;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;

@Data
public class AsbiepNode {

    @Data
    public class Asccp {
        private String asccpManifestId;
        private String guid;
        private String propertyTerm;
        private String den;
        private String definition;
        private CcState state;
        private boolean nillable;
    }

    private Asccp asccp = new Asccp();

    @Data
    public static class Asbiep {
        private boolean used;
        private boolean derived;
        private String path;
        private String hashPath;
        private String roleOfAbiePath;
        private String roleOfAbieHashPath;
        private String basedAsccpManifestId;
        private String refTopLevelAsbiepId;

        private String asbiepId;
        private String guid;
        private String remark;
        private String bizTerm;
        private String definition;
    }

    private Asbiep asbiep = new Asbiep();

}
