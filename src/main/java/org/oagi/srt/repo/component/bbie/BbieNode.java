package org.oagi.srt.repo.component.bbie;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

@Data
public class BbieNode {

    @Data
    public class Bcc {
        private BigInteger bccManifestId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private String den;
        private String definition;
        private String defaultValue;
        private String fixedValue;
        private boolean deprecated;
        private boolean nillable;
        private CcState state;
    }

    private Bcc bcc = new Bcc();

    @Data
    public class Bbie {
        private boolean used;
        private String hashPath;
        private BigInteger basedBccManifestId;

        private BigInteger bbieId;
        private String guid;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean nillable;
        private String remark;
        private String definition;
        private String defaultValue;
        private String fixedValue;
        private String example;

        private BigInteger bdtPriRestriId;
        private BigInteger codeListId;
        private BigInteger agencyIdListId;
    }

    private Bbie bbie = new Bbie();

}
