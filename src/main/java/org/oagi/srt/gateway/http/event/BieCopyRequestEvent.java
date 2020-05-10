package org.oagi.srt.gateway.http.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oagi.srt.redis.event.Event;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BieCopyRequestEvent implements Event {

    private BigInteger sourceTopLevelAbieId;
    private BigInteger copiedTopLevelAbieId;
    private List<BigInteger> bizCtxIds;
    private BigInteger userId;

}
