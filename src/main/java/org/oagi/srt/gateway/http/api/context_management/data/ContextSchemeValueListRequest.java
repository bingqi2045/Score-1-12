package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

@Data
public class ContextSchemeValueListRequest {

    private String value;

    private PageRequest pageRequest;

}
