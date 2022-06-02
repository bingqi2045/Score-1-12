package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.OagisComponentType;
import org.oagi.score.service.common.data.PageRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class CcListRequest {

    private BigInteger releaseId = BigInteger.ZERO;
    private CcListTypes types;
    private List<CcState> states;
    private Boolean deprecated;
    private List<String> ownerLoginIds;
    private List<String> updaterLoginIds;
    private List<String> dtTypes;
    private List<String> asccpTypes;
    private String den;
    private String definition;
    private String module;
    private List<OagisComponentType> componentTypes;
    private List<OagisComponentType> oagisEntities;
    private List<String> excludes;
    private Boolean isBIEUsable;
    private Boolean commonlyUsed;

    private Date updateStartDate;
    private Date updateEndDate;
    private PageRequest pageRequest = PageRequest.EMPTY_INSTANCE;

    private Map<BigInteger, String> usernameMap;

    public List<String> getDtTypes() {
        if (dtTypes != null && dtTypes.size() == 1) {
            return dtTypes;
        }
        return Collections.emptyList();
    }
}
