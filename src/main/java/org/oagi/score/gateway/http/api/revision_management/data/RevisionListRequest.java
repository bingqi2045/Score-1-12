package org.oagi.score.gateway.http.api.revision_management.data;

import lombok.Data;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

@Data
public class RevisionListRequest {
    String reference;
    PageRequest pageRequest;
}
