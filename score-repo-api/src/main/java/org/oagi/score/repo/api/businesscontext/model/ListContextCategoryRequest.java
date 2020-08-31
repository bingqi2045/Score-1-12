package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.base.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class ListContextCategoryRequest extends PaginationRequest<ContextCategory> {

    private Collection<BigInteger> contextCategoryIds;
    private String name;
    private String description;
    private Collection<String> updaterUsernames;
    private LocalDateTime updateStartDate;
    private LocalDateTime updateEndDate;

    public ListContextCategoryRequest(ScoreUser requester) {
        super(requester, ContextCategory.class);
    }

    public Collection<BigInteger> getContextCategoryIds() {
        return (contextCategoryIds == null) ? Collections.emptyList() : contextCategoryIds;
    }

    public void setContextCategoryIds(Collection<BigInteger> contextCategoryIds) {
        this.contextCategoryIds = contextCategoryIds;
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

    public Collection<String> getUpdaterUsernames() {
        return (updaterUsernames == null) ? Collections.emptyList() : updaterUsernames;
    }

    public void setUpdaterUsernames(Collection<String> updaterUsernames) {
        this.updaterUsernames = updaterUsernames;
    }

    public LocalDateTime getUpdateStartDate() {
        return updateStartDate;
    }

    public void setUpdateStartDate(LocalDateTime updateStartDate) {
        this.updateStartDate = updateStartDate;
    }

    public LocalDateTime getUpdateEndDate() {
        return updateEndDate;
    }

    public void setUpdateEndDate(LocalDateTime updateEndDate) {
        this.updateEndDate = updateEndDate;
    }

}
