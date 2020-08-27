package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.CoreComponentRepository;

import java.time.LocalDateTime;

public class InsertAsccpArguments {

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
    private ULong revisionId;
    private Boolean isNillable;
    private ULong prevAsccpId;
    private ULong nextAsccpId;

    public InsertAsccpArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getAsccpId() {
        return asccpId;
    }

    public ULong getRoleOfAccId() {
        return roleOfAccId;
    }

    public InsertAsccpArguments setRoleOfAccId(ULong roleOfAccId) {
        this.roleOfAccId = roleOfAccId;
        return this;
    }

    public Boolean getReuseableIndicator() {
        return reuseableIndicator;
    }

    public InsertAsccpArguments setReuseableIndicator(Boolean reuseableIndicator) {
        this.reuseableIndicator = reuseableIndicator;
        return this;
    }

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public InsertAsccpArguments setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
        return this;
    }

    public Boolean getNillable() {
        return isNillable;
    }

    public InsertAsccpArguments setNillable(Boolean nillable) {
        isNillable = nillable;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public InsertAsccpArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public InsertAsccpArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public InsertAsccpArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public InsertAsccpArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public InsertAsccpArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public InsertAsccpArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public InsertAsccpArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public InsertAsccpArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public InsertAsccpArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public InsertAsccpArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public InsertAsccpArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public ULong getRevisionId() {
        return revisionId;
    }

    public InsertAsccpArguments setRevisionId(ULong revisionId) {
        this.revisionId = revisionId;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public InsertAsccpArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isNillable;
    }

    public InsertAsccpArguments setAbstract(Boolean anAbstract) {
        isNillable = anAbstract;
        return this;
    }

    public ULong getPrevAsccpId() {
        return prevAsccpId;
    }

    public InsertAsccpArguments setPrevAsccpId(ULong prevAsccpId) {
        this.prevAsccpId = prevAsccpId;
        return this;
    }

    public ULong getNextAsccpId() {
        return nextAsccpId;
    }

    public InsertAsccpArguments setNextAsccpId(ULong nextAsccpId) {
        this.nextAsccpId = nextAsccpId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
