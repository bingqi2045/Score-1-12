package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.OagisComponentType;

import java.util.Date;
import java.util.List;

@Data
public class CcRefactorValidationResponse {

    private String type;

    private String manifestId;

    private List<IssuedCc> issueList;

    @Data
    public static class IssuedCc {
        private String manifestId;
        private String guid;
        private String den;
        private String name;

        private OagisComponentType oagisComponentType;
        private String owner;
        private CcState state;
        private String revision;
        private boolean deprecated;
        private String lastUpdateUser;
        private Date lastUpdateTimestamp;
        private String releaseNum;
        private String id;

        private List<String> reasons;
    }
}
