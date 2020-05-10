package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAccNode extends CcNode {

    private String type = "acc";
    private BigInteger accId = BigInteger.ZERO;
    private String den;
    private String guid;
    private String objectClassTerm;
    private BigInteger basedAccManifestId = BigInteger.ZERO;
    private int oagisComponentType;
    private String definition;
    private boolean group;
    private boolean isDeprecated;
    private boolean isAbstract;
    private BigInteger manifestId = BigInteger.ZERO;

    @Override
    public BigInteger getId() {
        return accId;
    }
}
