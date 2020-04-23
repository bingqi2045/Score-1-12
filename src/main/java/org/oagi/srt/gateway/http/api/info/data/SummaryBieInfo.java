package org.oagi.srt.gateway.http.api.info.data;

import lombok.Data;
import org.oagi.srt.data.BieState;

import java.util.List;
import java.util.Map;

@Data
public class SummaryBieInfo {

    private Map<BieState, Integer> numberOfBieByStates;
    private Map<String, Integer> numberOfBieByUsers;
    private List<SummaryBie> recentlyWorkedOn;

}
