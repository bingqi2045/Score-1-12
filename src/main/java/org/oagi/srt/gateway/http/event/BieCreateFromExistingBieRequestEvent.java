package org.oagi.srt.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.srt.redis.event.Event;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BieCreateFromExistingBieRequestEvent implements Event {

    private BigInteger sourceTopLevelAsbiepId;
    private BigInteger targetTopLevelAsbiepId;
    private BigInteger asbiepId;
    private List<BigInteger> bizCtxIds = Collections.emptyList();
    private BigInteger userId;

}