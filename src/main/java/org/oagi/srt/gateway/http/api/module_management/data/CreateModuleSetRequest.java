package org.oagi.srt.gateway.http.api.module_management.data;

import lombok.Data;

@Data
public class CreateModuleSetRequest {

    private String name;
    private String description;

}