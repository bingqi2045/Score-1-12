package org.oagi.srt.gateway.http.context_management;

public class ContextCategory {

    private long ctxCategoryId;
    private String guid;
    private String name;
    private String description;

    public long getCtxCategoryId() {
        return ctxCategoryId;
    }

    public void setCtxCategoryId(long ctxCategoryId) {
        this.ctxCategoryId = ctxCategoryId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
