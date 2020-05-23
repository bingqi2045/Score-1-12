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
public class ReleaseCreateRequestEvent implements Event {

    private BigInteger userId;
    private BigInteger releaseId;

    private List<BigInteger> accManifestIds = Collections.emptyList();
    private List<BigInteger> asccpManifestIds = Collections.emptyList();
    private List<BigInteger> bccpManifestIds = Collections.emptyList();

}
