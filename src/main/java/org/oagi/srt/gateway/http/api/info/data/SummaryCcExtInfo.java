package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class SummaryCcExtInfo {

    private Map<CcState, Integer> numberOfUegByStates;
    private Map<String, Integer> numberOfUegByUsers;

    private Map<Long, List<SummaryBieForCcExt>> summaryCcExtListMap;

}
