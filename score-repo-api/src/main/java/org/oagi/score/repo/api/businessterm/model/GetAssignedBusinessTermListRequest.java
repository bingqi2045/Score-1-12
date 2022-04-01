package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.PaginationRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class GetAssignedBusinessTermListRequest extends PaginationRequest<BusinessTerm> {

    private Collection<BigInteger> assignedBtIdList;
    private String businessTerm;
    private String externalReferenceUri;
    private BigInteger bieId;
    private String bieDen;
    private List<String> bieTypes;
    private String isPrimary;
    private String typeCode;
    private String searchByCC;
    private Collection<String> updaterUsernameList;
    private LocalDateTime updateStartDate;
    private LocalDateTime updateEndDate;

    public GetAssignedBusinessTermListRequest(ScoreUser requester) {
        super(requester, BusinessTerm.class);
    }

    public Collection<BigInteger> getAssignedBtIdList() {
        return assignedBtIdList;
    }

    public void setAssignedBtIdList(Collection<BigInteger> assignedBtIdList) {
        this.assignedBtIdList = assignedBtIdList;
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

    public String getBieDen() {
        return bieDen;
    }

    public void setBieDen(String bieDen) {
        this.bieDen = bieDen;
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

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSearchByCC() {
        return searchByCC;
    }

    public void setSearchByCC(String searchByCC) {
        this.searchByCC = searchByCC;
    }

    public List<String> getBieTypes() {
        return bieTypes;
    }

    public void setBieTypes(List<String> bieTypes) {
        this.bieTypes = bieTypes;
    }

    public String getExternalReferenceUri() {
        return externalReferenceUri;
    }

    public void setExternalReferenceUri(String externalReferenceUri) {
        this.externalReferenceUri = externalReferenceUri;
    }
}
