package org.oagi.score.service.log.model;

import lombok.Data;
import org.oagi.score.service.common.data.PageRequest;

@Data
public class LogListRequest {

    private String reference;
    private String type;
    private String manifestId;

    private PageRequest pageRequest;

}
