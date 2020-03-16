package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.tables.Bccp.BCCP;

public class InsertBccpArguments {

    private ULong bccpId;
    private String guid;
    private String propertyTerm;
    private String representationTerm;
    private ULong bdtId;
    private String den;
    private String definition;
    private String definitionSource;
    private ULong namespaceId;
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
    private Boolean isNillable;
    private String defaultValue;
    private String fixedValue;
    private ULong prevBccpId;
    private ULong nextBccpId;

    public String getPropertyTerm() {
        return propertyTerm;
    }

    public InsertBccpArguments setPropertyTerm(String propertyTerm) {
        this.propertyTerm = propertyTerm;
        return this;
    }

    public String getRepresentationTerm() {
        return representationTerm;
    }

    public InsertBccpArguments setRepresentationTerm(String representationTerm) {
        this.representationTerm = representationTerm;
        return this;
    }

    public ULong getBdtId() {
        return bdtId;
    }

    public InsertBccpArguments setBdtId(ULong bdtId) {
        this.bdtId = bdtId;
        return this;
    }

    public Boolean getNillable() {
        return isNillable;
    }

    public InsertBccpArguments setNillable(Boolean nillable) {
        isNillable = nillable;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public InsertBccpArguments setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public InsertBccpArguments setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
        return this;
    }

    public ULong getBccpId() {
        return bccpId;
    }

    public InsertBccpArguments setBccpId(ULong bccpId) {
        this.bccpId = bccpId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public InsertBccpArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getDen() {
        return den;
    }

    public InsertBccpArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public InsertBccpArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public InsertBccpArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public InsertBccpArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public InsertBccpArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public InsertBccpArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public InsertBccpArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public InsertBccpArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public InsertBccpArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public InsertBccpArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Integer getRevisionNum() {
        return revisionNum;
    }

    public InsertBccpArguments setRevisionNum(Integer revisionNum) {
        this.revisionNum = revisionNum;
        return this;
    }

    public Integer getRevisionTrackingNum() {
        return revisionTrackingNum;
    }

    public InsertBccpArguments setRevisionTrackingNum(Integer revisionTrackingNum) {
        this.revisionTrackingNum = revisionTrackingNum;
        return this;
    }

    public RevisionAction getRevisionAction() {
        return revisionAction;
    }

    public InsertBccpArguments setRevisionAction(RevisionAction revisionAction) {
        this.revisionAction = revisionAction;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public InsertBccpArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isNillable;
    }

    public InsertBccpArguments setAbstract(Boolean anAbstract) {
        isNillable = anAbstract;
        return this;
    }

    public ULong getPrevBccpId() {
        return prevBccpId;
    }

    public InsertBccpArguments setPrevBccpId(ULong prevBccpId) {
        this.prevBccpId = prevBccpId;
        return this;
    }

    public ULong getNextBccpId() {
        return nextBccpId;
    }

    public InsertBccpArguments setNextBccpId(ULong nextBccpId) {
        this.nextBccpId = nextBccpId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return insertBccp(dslContext, this);
    }

    private ULong insertBccp(DSLContext dslContext, InsertBccpArguments arguments) {
        return dslContext.insertInto(BCCP)
                .set(BCCP.BCCP_ID, arguments.getBccpId())
                .set(BCCP.GUID, arguments.getGuid())
                .set(BCCP.PROPERTY_TERM, arguments.getPropertyTerm())
                .set(BCCP.REPRESENTATION_TERM, arguments.getRepresentationTerm())
                .set(BCCP.BDT_ID, arguments.getBdtId())
                .set(BCCP.DEN, arguments.getDen())
                .set(BCCP.DEFINITION, arguments.getDefinition())
                .set(BCCP.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(BCCP.NAMESPACE_ID, arguments.getNamespaceId())
                .set(BCCP.CREATED_BY, arguments.getCreatedBy())
                .set(BCCP.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(BCCP.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(BCCP.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(BCCP.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(BCCP.STATE, arguments.getState().name())
                .set(BCCP.REVISION_NUM, arguments.getRevisionNum())
                .set(BCCP.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(BCCP.REVISION_ACTION, arguments.getRevisionAction().getValue())
                .set(BCCP.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(BCCP.IS_NILLABLE, arguments.getNillable() ? (byte) 1: 0)
                .set(BCCP.DEFAULT_VALUE, arguments.getDefaultValue())
                .set(BCCP.FIXED_VALUE, arguments.getFixedValue())
                .set(BCCP.PREV_BCCP_ID, arguments.getPrevBccpId())
                .set(BCCP.NEXT_BCCP_ID, arguments.getNextBccpId()).returning(BCCP.BCCP_ID).fetchOne().getBccpId();
    }
}
