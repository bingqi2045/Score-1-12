package org.oagi.srt.repo.component.asbie;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

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
    public class Asbie {
        private boolean used;

        private BigInteger asbieId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean nillable;
        private String remark;
        private String definition;
    }

    private Asbie asbie = new Asbie();

}
