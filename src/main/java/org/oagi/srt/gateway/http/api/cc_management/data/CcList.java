package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.jooq.tools.StringUtils;
import org.oagi.srt.data.OagisComponentType;

import java.math.BigInteger;
import java.util.Date;

@Data
public class CcList {

    private String type;
    private BigInteger manifestId;
    private String guid;
    private String den;
    private String definition;
    private String module;

    public String getModule() {
        return StringUtils.isEmpty(module) ? "" : module;
    }

    private String definitionSource;
    private OagisComponentType oagisComponentType;
    private String owner;
    private CcState state;
    private String revision;
    private boolean deprecated;
    private String lastUpdateUser;
    private Date lastUpdateTimestamp;
    private String releaseNum;
    private BigInteger id;
}
