package org.oagi.srt.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.srt.redis.event.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BieCopyRequestEvent implements Event {

    private long sourceTopLevelAbieId;
    private long copiedTopLevelAbieId;
    private long bizCtxId;
    private long userId;

}
