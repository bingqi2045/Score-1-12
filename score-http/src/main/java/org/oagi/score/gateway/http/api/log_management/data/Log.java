package org.oagi.score.gateway.http.api.log_management.data;

import lombok.Data;
import org.oagi.score.data.LogAction;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Log {
    private BigInteger logId;
    private String commit;
    private int revisionNum;
    private int revisionTrackingNum;
    private LogAction logAction;
    private String loginId;
    private LocalDateTime timestamp;
    private BigInteger prevLogId;
}
