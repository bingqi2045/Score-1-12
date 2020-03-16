package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.tables.Acc.ACC;

public class InsertAccArguments {

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
    private Integer revisionNum;
    private Integer revisionTrackingNum;
    private RevisionAction revisionAction;
    private Boolean isDeprecated;
    private Boolean isAbstract;
    private ULong prevAccId;
    private ULong nextAccId;

    public ULong getAccId() {
        return accId;
    }

    public InsertAccArguments setAccId(ULong accId) {
        this.accId = accId;
        return this;
    }

    public String getGuid() {
        return guid;
    }

    public InsertAccArguments setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public String getObjectClassTerm() {
        return objectClassTerm;
    }

    public InsertAccArguments setObjectClassTerm(String objectClassTerm) {
        this.objectClassTerm = objectClassTerm;
        return this;
    }

    public String getDen() {
        return den;
    }

    public InsertAccArguments setDen(String den) {
        this.den = den;
        return this;
    }

    public String getDefinition() {
        return definition;
    }

    public InsertAccArguments setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public InsertAccArguments setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
        return this;
    }

    public ULong getBasedAccId() {
        return basedAccId;
    }

    public InsertAccArguments setBasedAccId(ULong basedAccId) {
        this.basedAccId = basedAccId;
        return this;
    }

    public String getObjectClassQualifier() {
        return objectClassQualifier;
    }

    public InsertAccArguments setObjectClassQualifier(String objectClassQualifier) {
        this.objectClassQualifier = objectClassQualifier;
        return this;
    }

    public OagisComponentType getOagisComponentType() {
        return oagisComponentType;
    }

    public InsertAccArguments setOagisComponentType(OagisComponentType oagisComponentType) {
        this.oagisComponentType = oagisComponentType;
        return this;
    }

    public ULong getNamespaceId() {
        return namespaceId;
    }

    public InsertAccArguments setNamespaceId(ULong namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public ULong getCreatedBy() {
        return createdBy;
    }

    public InsertAccArguments setCreatedBy(ULong createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public ULong getOwnerUserId() {
        return ownerUserId;
    }

    public InsertAccArguments setOwnerUserId(ULong ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public ULong getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public InsertAccArguments setLastUpdatedBy(ULong lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
        return this;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public InsertAccArguments setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public LocalDateTime getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public InsertAccArguments setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        return this;
    }

    public CcState getState() {
        return state;
    }

    public InsertAccArguments setState(CcState state) {
        this.state = state;
        return this;
    }

    public Integer getRevisionNum() {
        return revisionNum;
    }

    public InsertAccArguments setRevisionNum(Integer revisionNum) {
        this.revisionNum = revisionNum;
        return this;
    }

    public Integer getRevisionTrackingNum() {
        return revisionTrackingNum;
    }

    public InsertAccArguments setRevisionTrackingNum(Integer revisionTrackingNum) {
        this.revisionTrackingNum = revisionTrackingNum;
        return this;
    }

    public RevisionAction getRevisionAction() {
        return revisionAction;
    }

    public InsertAccArguments setRevisionAction(RevisionAction revisionAction) {
        this.revisionAction = revisionAction;
        return this;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public InsertAccArguments setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
        return this;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public InsertAccArguments setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
        return this;
    }

    public ULong getPrevAccId() {
        return prevAccId;
    }

    public InsertAccArguments setPrevAccId(ULong prevAccId) {
        this.prevAccId = prevAccId;
        return this;
    }

    public ULong getNextAccId() {
        return nextAccId;
    }

    public InsertAccArguments setNextAccId(ULong nextAccId) {
        this.nextAccId = nextAccId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return insertAcc(dslContext, this);
    }

    private ULong insertAcc(DSLContext dslContext, InsertAccArguments arguments) {
        if (arguments.getDeprecated() == null) {
            arguments.setDeprecated(false);
        }
        if (arguments.getAbstract() == null) {
            arguments.setAbstract(false);
        }

        return dslContext.insertInto(ACC)
                .set(ACC.ACC_ID, arguments.getAccId())
                .set(ACC.GUID, arguments.getGuid())
                .set(ACC.OBJECT_CLASS_TERM, arguments.getObjectClassTerm())
                .set(ACC.DEN, arguments.getDen())
                .set(ACC.DEFINITION, arguments.getDefinition())
                .set(ACC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ACC.OBJECT_CLASS_QUALIFIER, arguments.getObjectClassQualifier())
                .set(ACC.OAGIS_COMPONENT_TYPE, arguments.getOagisComponentType().getValue())
                .set(ACC.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ACC.CREATED_BY, arguments.getCreatedBy())
                .set(ACC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ACC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ACC.CREATION_TIMESTAMP, arguments.getCreationTimestamp())
                .set(ACC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ACC.STATE, arguments.getState().name())
                .set(ACC.REVISION_NUM, arguments.getRevisionNum())
                .set(ACC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ACC.REVISION_ACTION, (byte) arguments.getRevisionAction().getValue())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1: 0)
                .set(ACC.PREV_ACC_ID, arguments.getPrevAccId())
                .set(ACC.NEXT_ACC_ID, arguments.getNextAccId()).returning(ACC.ACC_ID).fetchOne().getAccId();
    }
}
