package org.oagi.score.service.log.model;

import lombok.Data;
import org.oagi.score.service.common.data.PageRequest;

import java.math.BigInteger;

@Data
public class LogListRequest {

    private String reference;
    private String type;
    private BigInteger manifestId;

    private PageRequest pageRequest;

}
