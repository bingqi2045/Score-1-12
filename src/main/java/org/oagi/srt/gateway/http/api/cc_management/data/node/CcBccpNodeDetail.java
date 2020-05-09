package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import org.oagi.srt.data.BCCEntityType;

import java.math.BigInteger;

@Data
public class CcBccpNodeDetail implements CcNodeDetail {

    private String type = "bccp";
    private Bcc bcc;
    private Bccp bccp;
    private Bdt bdt;

    @Data
    public static class Bcc {
        private BigInteger bccId = BigInteger.ZERO;
        private BigInteger manifestId = BigInteger.ZERO;
        private String guid;
        private String den;
        private BCCEntityType entityType;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean deprecated;
        private boolean nillable;
        private String defaultValue;
        private String fixedValue;
        private String definition;
        private String definitionSource;
        private int revisionNum;
    }

    @Data
    public static class Bccp {
        private BigInteger bccpId = BigInteger.ZERO;
        private BigInteger manifestId = BigInteger.ZERO;
        private String guid;
        private String propertyTerm;
        private String den;
        private boolean nillable;
        private boolean deprecated;
        private BigInteger namespaceId = BigInteger.ZERO;
        private String defaultValue;
        private String fixedValue;
        private String definition;
        private String definitionSource;
    }

    @Data
    public static class Bdt {
        private BigInteger bdtId = BigInteger.ZERO;
        private String guid;
        private String dataTypeTerm;
        private String qualifier;
        private String den;
        private String definition;
        private String definitionSource;
    }
}
