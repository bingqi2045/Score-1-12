package org.oagi.srt.gateway.http.bie_management;

import java.util.Collections;
import java.util.List;

public class DeleteBieListRequest {

    private List<Long> topLevelAbieIds;

    public List<Long> getTopLevelAbieIds() {
        return (topLevelAbieIds != null) ? topLevelAbieIds : Collections.emptyList();
    }

    public void setTopLevelAbieIds(List<Long> topLevelAbieIds) {
        this.topLevelAbieIds = topLevelAbieIds;
    }
}
