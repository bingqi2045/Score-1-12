package org.oagi.srt.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.CoreComponentRepository;

import java.time.LocalDateTime;
import java.util.Objects;

public class UpdateAsccArguments {

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
    private Integer revisionNum;
    private Integer revisionTrackingNum;
    private RevisionAction revisionAction;
    private ULong prevAsccId;
    private ULong nextAsccId;

    private int _hashCode;

    public UpdateAsccArguments(CoreComponentRepository repository, AsccRecord ascc) {
        this.repository = repository;
        this.asccId = ascc.getAsccId();
        this.guid = ascc.getGuid();
        this.den = ascc.getDen();
        this.cardinalityMin = ascc.getCardinalityMin();
        this.cardinalityMax = ascc.getCardinalityMax();
        this.seqKey = ascc.getSeqKey();
        this.fromAccId = ascc.getFromAccId();
        this.toAsccpId = ascc.getToAsccpId();
        this.definition = ascc.getDefinition();
        this.definitionSource = ascc.getDefinitionSource();
        this.isDeprecated = ascc.getIsDeprecated() == 1;
        this.createdBy = ascc.getCreatedBy();
        this.ownerUserId = ascc.getOwnerUserId();
        this.lastUpdatedBy = ascc.getLastUpdatedBy();
        this.creationTimestamp = ascc.getCreationTimestamp();
        this.lastUpdateTimestamp = ascc.getLastUpdateTimestamp();
        this.state = CcState.valueOf(ascc.getState());
        this.prevAsccId = ascc.getPrevAsccId();

        this._hashCode = this.hashCode();
    }

    public ULong getAsccId() {
        return asccId;
    }

    public UpdateAsccArguments setAsccId(ULong asccId) {
        this.asccId = asccId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public UpdateAsccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public UpdateAsccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public Integer getCardinalityMin() {
        return cardinalityMin;
    }

    public UpdateAsccArguments setCardinalityMin(Integer cardinalityMin) {
        this.cardinalityMin = cardinalityMin;
        return this;
    }

    public Integer getCardinalityMax() {
        return cardinalityMax;
    }

    public UpdateAsccArguments setCardinalityMax(Integer cardinalityMax) {
        this.cardinalityMax = cardinalityMax;
        return this;
    }

    public Integer getSeqKey() {
        return seqKey;
    }

    public UpdateAsccArguments setSeqKey(Integer seqKey) {
        this.seqKey = seqKey;
        return this;
    }

    public ULong getFromAccId() {
        return fromAccId;
    }

    public UpdateAsccArguments setFromAccId(ULong fromAccId) {
        this.fromAccId = fromAccId;
        return this;
    }

    public ULong getToAsccpId() {
        return toAsccpId;
    }

    public UpdateAsccArguments setToAsccpId(ULong toAsccpId) {
        this.toAsccpId = toAsccpId;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public UpdateAsccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public UpdateAsccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public UpdateAsccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public UpdateAsccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public UpdateAsccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public UpdateAsccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public UpdateAsccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public UpdateAsccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public UpdateAsccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Integer getRevisionNum() {
        return revisionNum;
    }

    public UpdateAsccArguments setRevisionNum(Integer revisionNum) {
        this.revisionNum = revisionNum;
        return this;
    }

    public Integer getRevisionTrackingNum() {
        return revisionTrackingNum;
    }

    public UpdateAsccArguments setRevisionTrackingNum(Integer revisionTrackingNum) {
        this.revisionTrackingNum = revisionTrackingNum;
        return this;
    }

    public RevisionAction getRevisionAction() {
        return revisionAction;
    }

    public UpdateAsccArguments setRevisionAction(RevisionAction revisionAction) {
        this.revisionAction = revisionAction;
        return this;
    }

    public ULong getPrevAsccId() {
        return prevAsccId;
    }

    public UpdateAsccArguments setPrevAsccId(ULong prevAsccId) {
        this.prevAsccId = prevAsccId;
        return this;
    }

    public ULong getNextAsccId() {
        return nextAsccId;
    }

    public UpdateAsccArguments setNextAsccId(ULong nextAsccId) {
        this.nextAsccId = nextAsccId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateAsccArguments that = (UpdateAsccArguments) o;
        return Objects.equals(cardinalityMax, that.cardinalityMax) &&
                Objects.equals(cardinalityMin, that.cardinalityMin) &&
                Objects.equals(seqKey, that.seqKey) &&
                Objects.equals(toAsccpId, that.toAsccpId) &&
                Objects.equals(fromAccId, that.fromAccId) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(definitionSource, that.definitionSource) &&
                Objects.equals(isDeprecated, that.isDeprecated) &&
                Objects.equals(ownerUserId, that.ownerUserId) &&
                Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
                Objects.equals(lastUpdateTimestamp, that.lastUpdateTimestamp) &&
                state == that.state &&
                Objects.equals(revisionNum, that.revisionNum) &&
                Objects.equals(revisionTrackingNum, that.revisionTrackingNum) &&
                revisionAction.equals(that.revisionAction) &&
                Objects.equals(prevAsccId, that.prevAsccId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardinalityMax, cardinalityMin, seqKey, definition, definitionSource, fromAccId, toAsccpId, isDeprecated, ownerUserId, lastUpdatedBy, lastUpdateTimestamp, state, revisionNum, revisionTrackingNum, revisionAction, prevAsccId);
    }

    private boolean isDirty() {
        return !Objects.equals(this._hashCode, this.hashCode());
    }

    public ULong execute() {
        if (!isDirty()) {
            return getPrevAsccId();
        }

        return repository.execute(this);
    }
}
