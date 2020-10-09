package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.*;

import static org.oagi.score.gateway.http.api.release_management.data.ReleaseValidationResponse.ValidationMessageLevel.Error;

@Data
public class ReleaseValidationResponse {

    public enum ValidationMessageLevel {
        Warn,
        Error
    }

    private class ValidationMessage {
        private ValidationMessageLevel level;
        private String message;

        public ValidationMessage(ValidationMessageLevel level, String message) {
            this.level = level;
            this.message = message;
        }

        public ValidationMessageLevel getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValidationMessage that = (ValidationMessage) o;
            return Objects.equals(level, that.level) &&
                    Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, message);
        }
    }

    private Map<BigInteger, Set<ValidationMessage>> statusMapForAcc = new HashMap();
    private Map<BigInteger, Set<ValidationMessage>> statusMapForAsccp = new HashMap();
    private Map<BigInteger, Set<ValidationMessage>> statusMapForBccp = new HashMap();
    private Map<BigInteger, Set<ValidationMessage>> statusMapForCodeList = new HashMap();

    public boolean isSucceed() {
        return (statusMapForAcc.isEmpty() || statusMapForAcc.values().stream().flatMap(e -> e.stream()).filter(e -> e.getLevel() == Error).count() == 0) &&
               (statusMapForAsccp.isEmpty() || statusMapForAsccp.values().stream().flatMap(e -> e.stream()).filter(e -> e.getLevel() == Error).count() == 0) &&
               (statusMapForBccp.isEmpty() || statusMapForBccp.values().stream().flatMap(e -> e.stream()).filter(e -> e.getLevel() == Error).count() == 0) &&
               (statusMapForCodeList.isEmpty() || statusMapForCodeList.values().stream().flatMap(e -> e.stream()).filter(e -> e.getLevel() == Error).count() == 0);
    }

    public void addMessageForAcc(BigInteger manifestId, ValidationMessageLevel level, String message) {
        addMessage(statusMapForAcc, manifestId, level, message);
    }

    public void addMessageForAsccp(BigInteger manifestId, ValidationMessageLevel level, String message) {
        addMessage(statusMapForAsccp, manifestId, level, message);
    }

    public void addMessageForBccp(BigInteger manifestId, ValidationMessageLevel level, String message) {
        addMessage(statusMapForBccp, manifestId, level, message);
    }

    public void addMessageForCodeList(BigInteger manifestId, ValidationMessageLevel level, String message) {
        addMessage(statusMapForCodeList, manifestId, level, message);
    }

    private void addMessage(Map<BigInteger, Set<ValidationMessage>> statusMap,
                            BigInteger manifestId, ValidationMessageLevel level, String message) {
        Set<ValidationMessage> messages;
        if (!statusMap.containsKey(manifestId)) {
            messages = new HashSet();
            statusMap.put(manifestId, messages);
        } else {
            messages = statusMap.get(manifestId);
        }

        messages.add(new ValidationMessage(level, message));
    }
}
