package org.oagi.srt.repo.component.asbie;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class AsbieNode {

    @Data
    public class Ascc {
        private BigInteger asccManifestId;
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
        private boolean used;
        private String hashPath;
        private String fromAbieHashPath;
        private String toAsbiepHashPath;
        private BigInteger basedAsccManifestId;

        private BigInteger asbieId;
        private String guid;
        private BigInteger seqKey;
        private Integer cardinalityMin;
        private Integer cardinalityMax;
        private boolean nillable;
        private String remark;
        private String definition;

        public boolean isEmptyCardinality() {
            return (cardinalityMin == null && cardinalityMax == null);
        }
    }

    private Asbie asbie = new Asbie();

}
