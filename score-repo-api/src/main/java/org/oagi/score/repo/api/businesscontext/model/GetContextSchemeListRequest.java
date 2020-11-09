package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class GetContextSchemeListRequest extends PaginationRequest<ContextScheme> {

    private Collection<BigInteger> contextSchemeIdList;
    private Collection<BigInteger> contextCategoryIdList;
    private String name;
    private String description;
    private Collection<String> updaterUsernameList;
    private LocalDateTime updateStartDate;
    private LocalDateTime updateEndDate;

    public GetContextSchemeListRequest(ScoreUser requester) {
        super(requester, ContextScheme.class);
    }

    public Collection<BigInteger> getContextSchemeIdList() {
        return (contextSchemeIdList == null) ? Collections.emptyList() : contextSchemeIdList;
    }

    public void setContextSchemeIdList(Collection<BigInteger> contextSchemeIdList) {
        this.contextSchemeIdList = contextSchemeIdList;
    }

    public Collection<BigInteger> getContextCategoryIdList() {
        return (contextCategoryIdList == null) ? Collections.emptyList() : contextCategoryIdList;
    }

    public void setContextCategoryIdList(Collection<BigInteger> contextCategoryIdList) {
        this.contextCategoryIdList = contextCategoryIdList;
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

    public Collection<String> getUpdaterUsernameList() {
        return (updaterUsernameList == null) ? Collections.emptyList() : updaterUsernameList;
    }

    public void setUpdaterUsernameList(Collection<String> updaterUsernameList) {
        this.updaterUsernameList = updaterUsernameList;
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
