package org.oagi.score.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.score.redis.event.Event;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BieCopyRequestEvent implements Event {

    private String sourceTopLevelAsbiepId;
    private String copiedTopLevelAsbiepId;
    private List<String> bizCtxIds;
    private String userId;

}
