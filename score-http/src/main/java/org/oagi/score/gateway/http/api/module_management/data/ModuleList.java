package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ModuleList {

    private String moduleId;
    private String module;
    private String namespace;
    private String ownerUserId;
    private String owner;
    private String lastUpdatedBy;
    private Date lastUpdateTimestamp;
    private boolean canEdit;

}
