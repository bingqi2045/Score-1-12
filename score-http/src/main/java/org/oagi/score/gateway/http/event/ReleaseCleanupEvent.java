package org.oagi.score.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.score.redis.event.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseCleanupEvent implements Event {

    private String userId;
    private String releaseId;

}
