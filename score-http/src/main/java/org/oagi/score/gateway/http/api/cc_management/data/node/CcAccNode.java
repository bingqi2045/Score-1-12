package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class CcAccNode extends CcNode {

    private CcType type = CcType.ACC;
    private String accId;
    private String den;
    private String guid;
    private String objectClassTerm;
    private String basedAccManifestId;
    private int oagisComponentType;
    private String definition;
    private boolean group;
    private boolean isDeprecated;
    private boolean isAbstract;
    private String manifestId;
    private boolean hasExtension;
    private String accType;

    @Override
    public String getId() {
        return accId;
    }
}
