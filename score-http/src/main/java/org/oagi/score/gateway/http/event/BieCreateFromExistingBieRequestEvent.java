package org.oagi.score.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.score.redis.event.Event;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BieCreateFromExistingBieRequestEvent implements Event {

    private String sourceTopLevelAsbiepId;
    private String targetTopLevelAsbiepId;
    private String asbiepId;
    private List<String> bizCtxIds = Collections.emptyList();
    private String userId;

}