package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

@Data
public class ContextCategory {

    private long ctxCategoryId;
    private String guid;
    private String name;
    private String description;

}
