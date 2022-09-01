package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class ModuleSetModule {

    private String moduleId;
    private String path;
    private String namespaceId;
    private String namespaceUri;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
    private boolean assigned;

}
