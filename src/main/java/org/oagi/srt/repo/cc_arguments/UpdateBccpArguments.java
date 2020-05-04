package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.BccpRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.CoreComponentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateBccpArguments {

    private final CoreComponentRepository repository;

    private final String guid;
    private final ULong createdBy;
    private final LocalDateTime creationTimestamp;
    private String propertyTerm;
    private String representationTerm;
    private ULong bdtId;
    private String definition;
    private String definitionSource;
    private ULong namespaceId;
    private Boolean isDeprecated;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private Boolean isNillable;
    private String defaultValue;
    private String fixedValue;
    private ULong prevBccpId;

    private int _hashCode;

    public UpdateBccpArguments(CoreComponentRepository repository, BccpRecord bccp) {
        this.repository = repository;

        this.guid = bccp.getGuid();
        this.createdBy = bccp.getCreatedBy();
        this.creationTimestamp = bccp.getCreationTimestamp();
        this.propertyTerm = bccp.getPropertyTerm();
        this.representationTerm = bccp.getRepresentationTerm();
        this.bdtId = bccp.getBdtId();
        this.definition = bccp.getDefinition();
        this.definitionSource = bccp.getDefinitionSource();
        this.namespaceId = bccp.getNamespaceId();
        this.isDeprecated = bccp.getIsDeprecated() == 1;
        this.ownerUserId = bccp.getOwnerUserId();
        this.lastUpdatedBy = bccp.getLastUpdatedBy();
        this.lastUpdateTimestamp = bccp.getLastUpdateTimestamp();
        this.state = CcState.valueOf(bccp.getState());
        this.isNillable = bccp.getIsNillable() == 1;
        this.defaultValue = bccp.getDefaultValue();
        this.fixedValue = bccp.getFixedValue();
        this.prevBccpId = bccp.getBccpId();

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

    public UpdateBccpArguments setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
        return this;
    }

    public String getRepresentationTerm() {
        return representationTerm;
    }

    public UpdateBccpArguments setRepresentationTerm(String representationTerm) {
        this.representationTerm = representationTerm;
        return this;
    }

    public ULong getBdtId() {
        return bdtId;
    }

    public UpdateBccpArguments setBdtId(ULong bdtId) {
        this.bdtId = bdtId;
        return this;
    }

    public Boolean getNillable() {
        return isNillable;
    }

    public UpdateBccpArguments setNillable(Boolean nillable) {
        isNillable = nillable;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public UpdateBccpArguments setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public UpdateBccpArguments setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public UpdateBccpArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public UpdateBccpArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public UpdateBccpArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public UpdateBccpArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public UpdateBccpArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public UpdateBccpArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public UpdateBccpArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public UpdateBccpArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isNillable;
    }

    public UpdateBccpArguments setAbstract(Boolean anAbstract) {
        isNillable = anAbstract;
        return this;
    }

    public ULong getPrevBccpId() {
        return prevBccpId;
    }

    public UpdateBccpArguments setPrevBccpId(ULong prevBccpId) {
        this.prevBccpId = prevBccpId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBccpArguments that = (UpdateBccpArguments) o;
        return Objects.equals(propertyTerm, that.propertyTerm) &&
                Objects.equals(representationTerm, that.representationTerm) &&
                Objects.equals(bdtId, that.bdtId) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(definitionSource, that.definitionSource) &&
                Objects.equals(namespaceId, that.namespaceId) &&
                Objects.equals(isDeprecated, that.isDeprecated) &&
                Objects.equals(ownerUserId, that.ownerUserId) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdateTimestamp, that.lastUpdateTimestamp) &&
                state == that.state &&
                Objects.equals(isNillable, that.isNillable) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(fixedValue, that.fixedValue) &&
                Objects.equals(prevBccpId, that.prevBccpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyTerm, representationTerm, bdtId, definition, definitionSource, namespaceId, isDeprecated, ownerUserId, lastUpdatedBy, lastUpdateTimestamp, state, isNillable, defaultValue, fixedValue, prevBccpId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public ULong execute() {
        if (!isDirty()) {
            return getPrevBccpId();
        }

        return repository.execute(this);
    }


}
