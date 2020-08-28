package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccRecord;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.CoreComponentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateAccArguments {

    private final CoreComponentRepository repository;

    private ULong accId;
    private String guid;
    private String objectClassTerm;
    private String den;
    private String definition;
    private String definitionSource;
    private ULong basedAccId;
    private String objectClassQualifier;
    private OagisComponentType oagisComponentType;
    private ULong namespaceId;
    private ULong createdBy;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private Boolean isDeprecated;
    private Boolean isAbstract;
    private ULong prevAccId;

    private int _hashCode;

    public UpdateAccArguments(CoreComponentRepository repository, AccRecord acc) {
        this.repository = repository;
        this.accId = acc.getAccId();
        this.guid = acc.getGuid();
        this.objectClassTerm = acc.getObjectClassTerm();
        this.den = acc.getDen();
        this.definition = acc.getDefinition();
        this.definitionSource = acc.getDefinitionSource();
        this.basedAccId = acc.getBasedAccId();
        this.objectClassQualifier = acc.getObjectClassQualifier();
        this.oagisComponentType = OagisComponentType.valueOf(acc.getOagisComponentType());
        this.namespaceId = acc.getNamespaceId();
        this.createdBy = acc.getCreatedBy();
        this.ownerUserId = acc.getOwnerUserId();
        this.lastUpdatedBy = acc.getLastUpdatedBy();
        this.creationTimestamp = acc.getCreationTimestamp();
        this.lastUpdateTimestamp = acc.getLastUpdateTimestamp();
        this.state = CcState.valueOf(acc.getState());
        this.isDeprecated = acc.getIsDeprecated() == 1;
        this.isAbstract = acc.getIsAbstract() == 1;
        this.prevAccId = acc.getPrevAccId();

        this._hashCode = this.hashCode();
    }

    public ULong getAccId() {
        return accId;
    }

    public UpdateAccArguments setAccId(ULong accId) {
        this.accId = accId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public UpdateAccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getObjectClassTerm() {
        return objectClassTerm;
    }

    public UpdateAccArguments setObjectClassTerm(String objectClassTerm) {
        this.objectClassTerm = objectClassTerm;
        return this;
    }

    public String getDen() {
        return den;
    }

    public UpdateAccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public UpdateAccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public UpdateAccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getBasedAccId() {
        return basedAccId;
    }

    public UpdateAccArguments setBasedAccId(ULong basedAccId) {
        this.basedAccId = basedAccId;
        return this;
    }

    public String getObjectClassQualifier() {
        return objectClassQualifier;
    }

    public UpdateAccArguments setObjectClassQualifier(String objectClassQualifier) {
        this.objectClassQualifier = objectClassQualifier;
        return this;
    }

    public OagisComponentType getOagisComponentType() {
        return oagisComponentType;
    }

    public UpdateAccArguments setOagisComponentType(OagisComponentType oagisComponentType) {
        this.oagisComponentType = oagisComponentType;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public UpdateAccArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public UpdateAccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public UpdateAccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public UpdateAccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public UpdateAccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public UpdateAccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public UpdateAccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public UpdateAccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public UpdateAccArguments setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
        return this;
    }

    public ULong getPrevAccId() {
        return prevAccId;
    }

    public UpdateAccArguments setPrevAccId(ULong prevAccId) {
        this.prevAccId = prevAccId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAccArguments that = (UpdateAccArguments) o;
        return Objects.equals(objectClassTerm, that.objectClassTerm) &&
                Objects.equals(objectClassQualifier, that.objectClassQualifier) &&
                Objects.equals(oagisComponentType, that.oagisComponentType) &&
                Objects.equals(namespaceId, that.namespaceId) &&
                Objects.equals(basedAccId, that.basedAccId) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(definitionSource, that.definitionSource) &&
                Objects.equals(namespaceId, that.namespaceId) &&
                Objects.equals(isDeprecated, that.isDeprecated) &&
                Objects.equals(isAbstract, that.isAbstract) &&
                Objects.equals(ownerUserId, that.ownerUserId) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdateTimestamp, that.lastUpdateTimestamp) &&
                state == that.state &&
                Objects.equals(prevAccId, that.prevAccId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectClassTerm, basedAccId, definition, definitionSource, namespaceId, isDeprecated, ownerUserId, lastUpdatedBy, lastUpdateTimestamp, state, isAbstract, prevAccId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public ULong execute() {
        if (!isDirty()) {
            return getAccId();
        }

        return repository.execute(this);
    }
}
