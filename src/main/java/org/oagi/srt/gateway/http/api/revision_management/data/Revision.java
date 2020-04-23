package org.oagi.srt.gateway.http.api.revision_management.data;

import lombok.Data;
import org.oagi.srt.data.RevisionAction;

import java.time.LocalDateTime;

@Data
public class Revision {
    private long revisionId;
    private long revisionNum;
    private long revisionTrackingNum;
    private RevisionAction revisionAction;
    private String reference;
    private String body;
    private String loginId;
    private LocalDateTime timestamp;
    private Long prevRevisionId;
}
