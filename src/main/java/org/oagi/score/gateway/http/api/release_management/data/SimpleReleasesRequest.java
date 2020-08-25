package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class SimpleReleasesRequest {

    private List<ReleaseState> states = Collections.emptyList();

}
