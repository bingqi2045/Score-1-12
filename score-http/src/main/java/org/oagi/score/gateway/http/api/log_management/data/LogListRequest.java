package org.oagi.score.gateway.http.api.log_management.data;

import lombok.Data;
import org.oagi.score.gateway.http.api.common.data.PageRequest;

@Data
public class LogListRequest {
    String reference;
    PageRequest pageRequest;
}
