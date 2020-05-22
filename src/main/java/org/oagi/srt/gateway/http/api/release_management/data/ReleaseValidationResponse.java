package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class ReleaseValidationResponse {

    private Map<BigInteger, Set<String>> statusMapForAcc = new HashMap();
    private Map<BigInteger, Set<String>> statusMapForAsccp = new HashMap();
    private Map<BigInteger, Set<String>> statusMapForBccp = new HashMap();

    public void addMessageForAcc(BigInteger manifestId, String message) {
        addMessage(statusMapForAcc, manifestId, message);
    }

    public void addMessageForAsccp(BigInteger manifestId, String message) {
        addMessage(statusMapForAsccp, manifestId, message);
    }

    public void addMessageForBccp(BigInteger manifestId, String message) {
        addMessage(statusMapForBccp, manifestId, message);
    }

    private void addMessage(Map<BigInteger, Set<String>> statusMap, BigInteger manifestId, String message) {
        Set<String> messages;
        if (!statusMap.containsKey(manifestId)) {
            messages = new HashSet();
            statusMap.put(manifestId, messages);
        } else {
            messages = statusMap.get(manifestId);
        }

        messages.add(message);
    }
}
