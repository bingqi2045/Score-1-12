package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SummaryBieForCcExt extends SummaryBie {

    private long accId;
    private String objectClassTerm;

}
