package org.oagi.srt.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.srt.redis.event.Event;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseCleanupEvent implements Event {

    private BigInteger userId;
    private BigInteger releaseId;

}
