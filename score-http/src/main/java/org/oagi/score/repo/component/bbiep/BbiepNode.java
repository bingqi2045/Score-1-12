package org.oagi.score.repo.component.bbiep;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;

@Data
public class BbiepNode {

    @Data
    public class Bccp {
        private String bccpManifestId;
        private String guid;
        private String propertyTerm;
        private String den;
        private String definition;
        private CcState state;
        private boolean nillable;
        private String defaultValue;
        private String fixedValue;
    }

    private Bccp bccp = new Bccp();

    @Data
    public class Bdt {
        private String guid;
        private String dataTypeTerm;
        private String den;
        private String definition;
        private CcState state;
    }

    private Bdt bdt = new Bdt();

    @Data
    public static class Bbiep {
        private boolean used;
        private String path;
        private String hashPath;
        private String basedBccpManifestId;

        private String bbiepId;
        private String guid;
        private String remark;
        private String bizTerm;
        private String definition;
    }

    private Bbiep bbiep = new Bbiep();

}
