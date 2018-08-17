package org.oagi.srt.gateway.http.api.cc_management.data;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CcListStates {

    public static List<CcState> fromString(String str) {
        if (str != null) {
            str = str.trim();
        }

        List<CcState> states = new ArrayList();
        if (!StringUtils.isEmpty(str)) {
            for (String state : str.split(",")) {
                states.add(CcState.valueOf(state));
            }
        }

        return states;
    }
}
