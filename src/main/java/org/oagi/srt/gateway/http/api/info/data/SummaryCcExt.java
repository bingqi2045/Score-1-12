package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.List;

@Data
public class SummaryCcExt {

    private long accId;
    private CcState state;
    private String objectClassTerm;
    private String ownerUsername;
    private long ownerUserId;

    private List<SummaryBieForCcExt> bieList;

}
