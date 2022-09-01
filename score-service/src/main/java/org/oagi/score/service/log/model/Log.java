package org.oagi.score.service.log.model;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class Log {

    public static final String CURRENT_LOG_HASH = "123e4567e89b12d3a456426614174000";

    private String logId;
    private String hash;
    private int revisionNum;
    private int revisionTrackingNum;
    private LogAction logAction;
    private String loginId;
    private LocalDateTime timestamp;
    private BigInteger prevLogId;
    private boolean isDeveloper;

}
