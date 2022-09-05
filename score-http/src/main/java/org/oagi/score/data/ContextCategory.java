package org.oagi.score.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class ContextCategory implements Serializable {

    private String ctxCategoryId;
    private String guid;
    private String name;
    private String description;
}
