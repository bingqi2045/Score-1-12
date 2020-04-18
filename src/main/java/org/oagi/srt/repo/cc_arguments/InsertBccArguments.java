package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.CoreComponentRepository;
import java.time.LocalDateTime;

public class InsertBccArguments {

    private final CoreComponentRepository repository;

    private ULong bccId;
    private String guid;
    private String den;
    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private Integer seqKey;
    private BCCEntityType entityType;
    private ULong fromAccId;
    private ULong toBccpId;
    private String definition;
    private String definitionSource;
    private Boolean isDeprecated;
    private Boolean isNillable;
    private String defaultValue;
    private String fixedValue;
    private ULong createdBy;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private ULong revisionId;
    private ULong prevBccId;
    private ULong nextBccId;

    public InsertBccArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getBccId() {
        return bccId;
    }

    public InsertBccArguments setBccId(ULong bccId) {
        this.bccId = bccId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public InsertBccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public InsertBccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public Integer getCardinalityMin() {
        return cardinalityMin;
    }

    public InsertBccArguments setCardinalityMin(Integer cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
        return this;
    }

    public Integer getCardinalityMax() {
        return cardinalityMax;
    }

    public InsertBccArguments setCardinalityMax(Integer cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
        return this;
    }

    public Integer getSeqKey() {
        return seqKey;
    }

    public InsertBccArguments setSeqKey(Integer seqKey) {
        this.seqKey = seqKey;
        return this;
    }

    public BCCEntityType getEntityType() {
        return entityType;
    }

    public InsertBccArguments setEntityType(BCCEntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public ULong getFromAccId() {
        return fromAccId;
    }

    public InsertBccArguments setFromAccId(ULong fromAccId) {
        this.fromAccId = fromAccId;
        return this;
    }

    public ULong getToBccpId() {
        return toBccpId;
    }

    public InsertBccArguments setToBccpId(ULong toBccpId) {
        this.toBccpId = toBccpId;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public InsertBccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public InsertBccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public InsertBccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getNillable() {
        return isNillable;
    }

    public InsertBccArguments setNillable(Boolean nillable) {
        isNillable = nillable;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public InsertBccArguments setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public InsertBccArguments setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public InsertBccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public InsertBccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public InsertBccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public InsertBccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public InsertBccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public InsertBccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public ULong getRevisionId() {
        return revisionId;
    }

    public InsertBccArguments setRevisionId(ULong revisionId) {
        this.revisionId = revisionId;
        return this;
    }

    public ULong getPrevBccId() {
        return prevBccId;
    }

    public InsertBccArguments setPrevBccId(ULong prevBccId) {
        this.prevBccId = prevBccId;
        return this;
    }

    public ULong getNextBccId() {
        return nextBccId;
    }

    public InsertBccArguments setNextBccId(ULong nextBccId) {
        this.nextBccId = nextBccId;
        return this;
    }

    public ULong execute() {
        return repository.execute(this);
    }
}
