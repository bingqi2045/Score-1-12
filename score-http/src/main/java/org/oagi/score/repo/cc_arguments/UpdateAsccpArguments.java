package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.entity.jooq.tables.records.AsccpRecord;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.CoreComponentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateAsccpArguments {

    private final CoreComponentRepository repository;

    private ULong asccpId;
    private String guid;
    private String propertyTerm;
    private String definition;
    private String definitionSource;
    private ULong roleOfAccId;
    private String den;
    private ULong createdBy;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private ULong namespaceId;
    private Boolean reuseableIndicator;
    private Boolean isDeprecated;
    private Boolean isNillable;
    private ULong prevAsccpId;
    private ULong nextAsccpId;

    private int _hashCode;

    public UpdateAsccpArguments(CoreComponentRepository repository, AsccpRecord asccp) {
        this.repository = repository;

        this.guid = asccp.getGuid();
        this.roleOfAccId = asccp.getRoleOfAccId();
        this.reuseableIndicator = asccp.getReusableIndicator() == 1;
        this.createdBy = asccp.getCreatedBy();
        this.creationTimestamp = asccp.getCreationTimestamp();
        this.propertyTerm = asccp.getPropertyTerm();
        this.den = asccp.getDen();
        this.definition = asccp.getDefinition();
        this.definitionSource = asccp.getDefinitionSource();
        this.namespaceId = asccp.getNamespaceId();
        this.isDeprecated = asccp.getIsDeprecated() == 1;
        this.ownerUserId = asccp.getOwnerUserId();
        this.lastUpdatedBy = asccp.getLastUpdatedBy();
        this.lastUpdateTimestamp = asccp.getLastUpdateTimestamp();
        this.state = CcState.valueOf(asccp.getState());
        this.isNillable = asccp.getIsNillable() == 1;
        this.prevAsccpId = asccp.getAsccpId();
        this.nextAsccpId = asccp.getNextAsccpId();

        this._hashCode = this.hashCode();
    }

    public String getGuid() {
        return guid;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public UpdateAsccpArguments setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
        return this;
    }

    public Boolean getNillable() {
        return isNillable;
    }

    public UpdateAsccpArguments setNillable(Boolean nillable) {
        isNillable = nillable;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public UpdateAsccpArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public UpdateAsccpArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public UpdateAsccpArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public UpdateAsccpArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public UpdateAsccpArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public UpdateAsccpArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public UpdateAsccpArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public UpdateAsccpArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isNillable;
    }

    public UpdateAsccpArguments setAbstract(Boolean anAbstract) {
        isNillable = anAbstract;
        return this;
    }

    public ULong getPrevAsccpId() {
        return prevAsccpId;
    }

    public UpdateAsccpArguments setPrevAsccpId(ULong prevAsccpId) {
        this.prevAsccpId = prevAsccpId;
        return this;
    }

    public ULong getAsccpId() {
        return asccpId;
    }

    public UpdateAsccpArguments setAsccpId(ULong asccpId) {
        this.asccpId = asccpId;
        return this;
    }

    public UpdateAsccpArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public ULong getRoleOfAccId() {
        return roleOfAccId;
    }

    public UpdateAsccpArguments setRoleOfAccId(ULong roleOfAccId) {
        this.roleOfAccId = roleOfAccId;
        return this;
    }

    public String getDen() {
        return den;
    }

    public UpdateAsccpArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public UpdateAsccpArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public UpdateAsccpArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public Boolean getReuseableIndicator() {
        return reuseableIndicator;
    }

    public UpdateAsccpArguments setReuseableIndicator(Boolean reuseableIndicator) {
        this.reuseableIndicator = reuseableIndicator;
        return this;
    }

    public ULong getNextAsccpId() {
        return nextAsccpId;
    }

    public UpdateAsccpArguments setNextAsccpId(ULong nextAsccpId) {
        this.nextAsccpId = nextAsccpId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAsccpArguments that = (UpdateAsccpArguments) o;
        return Objects.equals(propertyTerm, that.propertyTerm) &&
                Objects.equals(roleOfAccId, that.roleOfAccId) &&
                Objects.equals(reuseableIndicator, that.reuseableIndicator) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(definitionSource, that.definitionSource) &&
                Objects.equals(namespaceId, that.namespaceId) &&
                Objects.equals(isDeprecated, that.isDeprecated) &&
                Objects.equals(ownerUserId, that.ownerUserId) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdateTimestamp, that.lastUpdateTimestamp) &&
                state == that.state &&
                Objects.equals(isNillable, that.isNillable) &&
                Objects.equals(prevAsccpId, that.prevAsccpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyTerm, roleOfAccId, definition, definitionSource, namespaceId, isDeprecated, ownerUserId, lastUpdatedBy, lastUpdateTimestamp, state, isNillable, reuseableIndicator, prevAsccpId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public ULong execute() {
        if (!isDirty()) {
            return getPrevAsccpId();
        }

        return repository.execute(this);
    }


}
