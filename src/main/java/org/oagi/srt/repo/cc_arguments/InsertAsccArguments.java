package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.CoreComponentRepository;

import java.time.LocalDateTime;

public class InsertAsccArguments {

    private final CoreComponentRepository repository;

    private ULong asccId;
    private String guid;
    private String den;
    private Integer cardinalityMin;
    private Integer cardinalityMax;
    private Integer seqKey;
    private ULong fromAccId;
    private ULong toAsccpId;
    private String definition;
    private String definitionSource;
    private Boolean isDeprecated;
    private ULong createdBy;
    private ULong ownerUserId;
    private ULong lastUpdatedBy;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdateTimestamp;
    private CcState state;
    private ULong revisionId;
    private ULong prevAsccId;
    private ULong nextAsccId;

    public InsertAsccArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

    public ULong getAsccId() {
        return asccId;
    }

    public InsertAsccArguments setAsccId(ULong asccId) {
        this.asccId = asccId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public InsertAsccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public InsertAsccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public Integer getCardinalityMin() {
        return cardinalityMin;
    }

    public InsertAsccArguments setCardinalityMin(Integer cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
        return this;
    }

    public Integer getCardinalityMax() {
        return cardinalityMax;
    }

    public InsertAsccArguments setCardinalityMax(Integer cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
        return this;
    }

    public Integer getSeqKey() {
        return seqKey;
    }

    public InsertAsccArguments setSeqKey(Integer seqKey) {
        this.seqKey = seqKey;
        return this;
    }

    public ULong getFromAccId() {
        return fromAccId;
    }

    public InsertAsccArguments setFromAccId(ULong fromAccId) {
        this.fromAccId = fromAccId;
        return this;
    }

    public ULong getToAsccpId() {
        return toAsccpId;
    }

    public InsertAsccArguments setToAsccpId(ULong toAsccpId) {
        this.toAsccpId = toAsccpId;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public InsertAsccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public InsertAsccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public InsertAsccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public InsertAsccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public InsertAsccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public InsertAsccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public InsertAsccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public InsertAsccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public InsertAsccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public ULong getRevisionId() {
        return revisionId;
    }

    public InsertAsccArguments setRevisionId(ULong revisionId) {
        this.revisionId = revisionId;
        return this;
    }

    public ULong getPrevAsccId() {
        return prevAsccId;
    }

    public InsertAsccArguments setPrevAsccId(ULong prevAsccId) {
        this.prevAsccId = prevAsccId;
        return this;
    }

    public ULong getNextAsccId() {
        return nextAsccId;
    }

    public InsertAsccArguments setNextAsccId(ULong nextAsccId) {
        this.nextAsccId = nextAsccId;
        return this;
    }

    public ULong execute() {
        return this.repository.execute(this);
    }
}
