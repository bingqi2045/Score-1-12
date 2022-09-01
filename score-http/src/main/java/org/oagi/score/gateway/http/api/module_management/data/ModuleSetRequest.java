package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ModuleSetRequest {
    public String name;
    public String description;
    public boolean createModuleSetRelease;
    public String targetReleaseId;
    public BigInteger targetModuleSetReleaseId;
}
