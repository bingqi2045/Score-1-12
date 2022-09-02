package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ExtensionUpdateResponse {

    private Map<String, Boolean> asccResults = new HashMap();
    private Map<String, Boolean> bccResults = new HashMap();

}
