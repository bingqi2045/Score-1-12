package org.oagi.srt.repo.component.bbie_sc;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.asbie.AsbieNode;

import java.math.BigInteger;

@Data
public class BbieScNode {

    @Data
    public class DtSc {
        private BigInteger dtScManifestId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private String propertyTerm;
        private String representationTerm;
        private String definition;
        private CcState state;
    }

    private DtSc dtSc = new DtSc();

    @Data
    public class BbieSc {
        private boolean used;
        private String hashPath;
        private BigInteger basedDtScManifestId;

        private BigInteger bbieScId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private String remark;
        private String bizTerm;
        private String definition;
        private String defaultValue;
        private String fixedValue;
        private String example;

        private BigInteger bdtScPriRestriId;
        private BigInteger codeListId;
        private BigInteger agencyIdListId;
    }

    private BbieSc bbieSc = new BbieSc();

}
