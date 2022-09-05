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
public class ReleaseCreateRequestEvent implements Event {

    private String userId;
    private String releaseId;

    private List<String> accManifestIds;
    private List<String> asccpManifestIds;
    private List<String> bccpManifestIds;
    private List<String> dtManifestIds;
    private List<String> codeListManifestIds;
    private List<String> agencyIdListManifestIds;

    public List<String> getAccManifestIds() {
        if (accManifestIds == null) {
            return Collections.emptyList();
        }
        return accManifestIds;
    }

    public List<String> getAsccpManifestIds() {
        if (asccpManifestIds == null) {
            return Collections.emptyList();
        }
        return asccpManifestIds;
    }

    public List<String> getBccpManifestIds() {
        if (bccpManifestIds == null) {
            return Collections.emptyList();
        }
        return bccpManifestIds;
    }

    public List<String> getDtManifestIds() {
        if (dtManifestIds == null) {
            return Collections.emptyList();
        }
        return dtManifestIds;
    }

    public List<String> getCodeListManifestIds() {
        if (codeListManifestIds == null) {
            return Collections.emptyList();
        }
        return codeListManifestIds;
    }

    public List<String> getAgencyIdListManifestIds() {
        if (agencyIdListManifestIds == null) {
            return Collections.emptyList();
        }
        return agencyIdListManifestIds;
    }
}
