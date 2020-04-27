package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.List;
import java.util.Map;

@Data
public class SummaryCcExtInfo {

    private Map<CcState, Integer> numberOfTotalCcExtByStates;
    private Map<CcState, Integer> numberOfMyCcExtByStates;
    

    private Map<String, List<SummaryBieForCcExt>> summaryCcExtListMap;

}
