package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.math.BigInteger;

@Data
public class CcAccNodeDetail implements CcNodeDetail {
    private String type = "acc";
    private BigInteger accId;
    private String guid;
    private String objectClassTerm;
    private String den;
    private int oagisComponentType;
    private boolean abstracted;
    private boolean deprecated;
    private String definition;
    private String definitionSource;
    private CcState state;
    private BigInteger manifestId;
    private BigInteger namespaceId;
}
