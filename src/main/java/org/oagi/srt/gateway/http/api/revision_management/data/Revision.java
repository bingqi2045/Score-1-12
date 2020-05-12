package org.oagi.srt.gateway.http.api.revision_management.data;

import lombok.Data;
import org.oagi.srt.data.RevisionAction;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Revision {
    private BigInteger revisionId;
    private int revisionNum;
    private int revisionTrackingNum;
    private RevisionAction revisionAction;
    private String loginId;
    private LocalDateTime timestamp;
    private BigInteger prevRevisionId;
}
