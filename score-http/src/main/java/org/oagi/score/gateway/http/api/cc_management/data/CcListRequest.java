package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.OagisComponentType;
import org.oagi.score.service.common.data.PageRequest;

import java.math.BigInteger;
import java.util.*;

@Data
public class CcListRequest {

    private String releaseId;
    private CcListTypes types;
    private List<CcState> states = Collections.emptyList();
    private Boolean deprecated;
    private List<String> ownerLoginIds = Collections.emptyList();
    private List<String> updaterLoginIds = Collections.emptyList();
    private List<String> dtTypes = Collections.emptyList();
    private List<String> asccpTypes = Collections.emptyList();
    private String den;
    private String definition;
    private String module;
    private List<OagisComponentType> componentTypes = Collections.emptyList();
    private List<BigInteger> ccTagIds = Collections.emptyList();
    private List<String> excludes = Collections.emptyList();
    private Boolean isBIEUsable;
    private Boolean commonlyUsed;

    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

    private Map<String, String> usernameMap;

    public List<String> getDtTypes() {
        if (dtTypes != null && dtTypes.size() == 1) {
            return dtTypes;
        }
        return Collections.emptyList();
    }

    public List<BigInteger> getCcTagIds() {
        if (this.ccTagIds == null || this.ccTagIds.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList(new HashSet(this.ccTagIds));
    }
}
