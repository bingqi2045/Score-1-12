package org.oagi.score.repo.component.asbie;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;

@Data
public class AsbieNode {

    @Data
    public class Ascc {
        private String asccManifestId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private String den;
        private String definition;
        private CcState state;
    }

    private Ascc ascc = new Ascc();

    @Data
    public static class Asbie {
        private Boolean used;
        private String path;
        private String hashPath;
        private String fromAbiePath;
        private String fromAbieHashPath;
        private String toAsbiepPath;
        private String toAsbiepHashPath;
        private String basedAsccManifestId;

        private String asbieId;
        private String guid;
        private BigInteger seqKey;
        private Integer cardinalityMin;
        private Integer cardinalityMax;
        private Boolean nillable;
        private String remark;
        private String definition;
    }

    private Asbie asbie = new Asbie();

}
