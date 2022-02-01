package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;

public class GetAssignedBusinessTermListRequest extends PaginationRequest<BusinessTerm> {

    private Collection<BigInteger> businessTermIdList;
    private String businessTerm;
    private BigInteger bieId;
    private String biePropertyTerm;
    private String definition;
    private Collection<String> updaterUsernameList;
    private LocalDateTime updateStartDate;
    private LocalDateTime updateEndDate;

    public GetAssignedBusinessTermListRequest(ScoreUser requester) {
        super(requester, BusinessTerm.class);
    }

    public Collection<BigInteger> getBusinessTermIdList() {
        return businessTermIdList;
    }

    public void setBusinessTermIdList(Collection<BigInteger> businessTermIdList) {
        this.businessTermIdList = businessTermIdList;
    }

    public String getBusinessTerm() {
        return businessTerm;
    }

    public void setBusinessTerm(String businessTerm) {
        this.businessTerm = businessTerm;
    }

    public BigInteger getBieId() {
        return bieId;
    }

    public void setBieId(BigInteger bieId) {
        this.bieId = bieId;
    }

    public String getBiePropertyTerm() {
        return biePropertyTerm;
    }

    public void setBiePropertyTerm(String biePropertyTerm) {
        this.biePropertyTerm = biePropertyTerm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Collection<String> getUpdaterUsernameList() {
        return updaterUsernameList;
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
