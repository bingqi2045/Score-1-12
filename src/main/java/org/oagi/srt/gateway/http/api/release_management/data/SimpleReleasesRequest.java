package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;
import org.oagi.srt.entity.jooq.enums.ReleaseState;

import java.util.Collections;
import java.util.List;

@Data
public class SimpleReleasesRequest {

    private List<ReleaseState> states = Collections.emptyList();

}
