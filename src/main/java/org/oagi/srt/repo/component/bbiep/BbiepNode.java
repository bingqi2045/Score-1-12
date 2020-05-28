package org.oagi.srt.repo.component.bbiep;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

@Data
public class BbiepNode {

    @Data
    public class Bccp {
        private BigInteger bccpManifestId;
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
    public class Bbiep {
        private boolean used;

        private BigInteger bbiepId;
        private String guid;
        private String remark;
        private String bizTerm;
        private String definition;
    }

    private Bbiep bbiep = new Bbiep();

}
