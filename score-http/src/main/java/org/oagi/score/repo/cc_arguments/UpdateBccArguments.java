package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.CoreComponentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateBccArguments {

    private final CoreComponentRepository repository;

    private ULong bccId;
    private String guid;
    private String den;
    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private Integer seqKey;
    private ULong fromAccId;
    private ULong toBccpId;
    private Integer entitiyType;
    private String defaultValue;
    private String fixedValue;
    private String definition;
    private String definitionSource;
    private Boolean isDeprecated;
    private ULong createdBy;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private ULong prevBccId;
    private ULong nextBccId;

    private int _hashCode;

    public UpdateBccArguments(CoreComponentRepository repository, BccRecord bcc) {
        this.repository = repository;
        this.bccId = bcc.getBccId();
        this.guid = bcc.getGuid();
        this.den = bcc.getDen();
        this.cardinalityMin = bcc.getCardinalityMin();
        this.cardinalityMax = bcc.getCardinalityMax();
        this.seqKey = bcc.getSeqKey();
        this.fromAccId = bcc.getFromAccId();
        this.toBccpId = bcc.getToBccpId();
        this.entitiyType = bcc.getEntityType();
        this.defaultValue = bcc.getDefaultValue();
        this.fixedValue = bcc.getFixedValue();
        this.definition = bcc.getDefinition();
        this.definitionSource = bcc.getDefinitionSource();
        this.isDeprecated = bcc.getIsDeprecated() == 1;
        this.createdBy = bcc.getCreatedBy();
        this.ownerUserId = bcc.getOwnerUserId();
        this.lastUpdatedBy = bcc.getLastUpdatedBy();
        this.creationTimestamp = bcc.getCreationTimestamp();
        this.lastUpdateTimestamp = bcc.getLastUpdateTimestamp();
        this.state = CcState.valueOf(bcc.getState());
        this.prevBccId = bcc.getPrevBccId();

        this._hashCode = this.hashCode();
    }

    public ULong getBccId() {
        return bccId;
    }

    public UpdateBccArguments setBccId(ULong bccId) {
        this.bccId = bccId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public UpdateBccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public UpdateBccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public Integer getCardinalityMin() {
        return cardinalityMin;
    }

    public UpdateBccArguments setCardinalityMin(Integer cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
        return this;
    }

    public Integer getCardinalityMax() {
        return cardinalityMax;
    }

    public UpdateBccArguments setCardinalityMax(Integer cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
        return this;
    }

    public Integer getSeqKey() {
        return seqKey;
    }

    public UpdateBccArguments setSeqKey(Integer seqKey) {
        this.seqKey = seqKey;
        return this;
    }

    public Integer getEntitiyType() {
        return entitiyType;
    }

    public UpdateBccArguments setEntitiyType(Integer entitiyType) {
        this.entitiyType = entitiyType;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public UpdateBccArguments setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public UpdateBccArguments setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
        return this;
    }

    public ULong getFromAccId() {
        return fromAccId;
    }

    public UpdateBccArguments setFromAccId(ULong fromAccId) {
        this.fromAccId = fromAccId;
        return this;
    }

    public ULong getToBccpId() {
        return toBccpId;
    }

    public UpdateBccArguments setToBccpId(ULong toBccpId) {
        this.toBccpId = toBccpId;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public UpdateBccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public UpdateBccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public UpdateBccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public UpdateBccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public UpdateBccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public UpdateBccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public UpdateBccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public UpdateBccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public UpdateBccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public ULong getPrevBccId() {
        return prevBccId;
    }

    public UpdateBccArguments setPrevBccId(ULong prevBccId) {
        this.prevBccId = prevBccId;
        return this;
    }

    public ULong getNextBccId() {
        return nextBccId;
    }

    public UpdateBccArguments setNextBccId(ULong nextBccId) {
        this.nextBccId = nextBccId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBccArguments that = (UpdateBccArguments) o;
        return Objects.equals(cardinalityMax, that.cardinalityMax) &&
                Objects.equals(cardinalityMin, that.cardinalityMin) &&
                Objects.equals(seqKey, that.seqKey) &&
                Objects.equals(toBccpId, that.toBccpId) &&
                Objects.equals(fromAccId, that.fromAccId) &&
                Objects.equals(entitiyType, that.entitiyType) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(fixedValue, that.fixedValue) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(definitionSource, that.definitionSource) &&
                Objects.equals(isDeprecated, that.isDeprecated) &&
                Objects.equals(ownerUserId, that.ownerUserId) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdateTimestamp, that.lastUpdateTimestamp) &&
                state == that.state &&
                Objects.equals(prevBccId, that.prevBccId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardinalityMax, cardinalityMin, seqKey, definition, definitionSource, fromAccId, toBccpId, entitiyType, defaultValue, fixedValue, isDeprecated, ownerUserId, lastUpdatedBy, lastUpdateTimestamp, state, prevBccId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public ULong execute() {
        if (!isDirty()) {
            return getPrevBccId();
        }

        return repository.execute(this);
    }
}
