package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.data.CcType;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class AssignableNode {
    private BigInteger manifestId;
    private CcType type;
    private CcState state;
    private String displayName;
    private String ownerUserId;
    private LocalDateTime timestamp;
    private String revision;
}
