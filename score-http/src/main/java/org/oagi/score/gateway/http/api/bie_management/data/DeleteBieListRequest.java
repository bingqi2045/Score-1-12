package org.oagi.score.gateway.http.api.bie_management.data;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class DeleteBieListRequest {

    private List<String> topLevelAsbiepIds = Collections.emptyList();

}
