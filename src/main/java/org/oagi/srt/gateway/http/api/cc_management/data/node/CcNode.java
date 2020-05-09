package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.common.data.AccessPrivilege;
import org.oagi.srt.gateway.http.api.common.data.TrackableImpl;

import java.math.BigInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CcNode extends TrackableImpl {

    private String guid;
    private String name;
    private CcState state;
    private boolean hasChild;
    private AccessPrivilege access;
    private BigInteger ownerUserId = BigInteger.ZERO;
}
