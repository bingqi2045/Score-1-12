package org.oagi.srt.repo.component.asbiep;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

@Data
public class AsbiepNode {

    @Data
    public class Asccp {
        private BigInteger asccpManifestId;
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
        private boolean locked;
        private String hashPath;
        private String roleOfAbieHashPath;
        private BigInteger basedAsccpManifestId;
        private BigInteger refTopLevelAbieId;

        private BigInteger asbiepId;
        private String guid;
        private String remark;
        private String bizTerm;
        private String definition;
    }

    private Asbiep asbiep = new Asbiep();

}
