package org.oagi.srt.repo.cc_arguments;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.tables.Acc.ACC;

@Repository
public class UpdateAccArguments {

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
    private Integer revisionAction;
    private Boolean isDeprecated;
    private Boolean isAbstract;
    private ULong prevAccId;
    private ULong nextAccId;

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

    public Integer getRevisionNum() {
        return revisionNum;
    }

    public UpdateAccArguments setRevisionNum(Integer revisionNum) {
        this.revisionNum = revisionNum;
        return this;
    }

    public Integer getRevisionTrackingNum() {
        return revisionTrackingNum;
    }

    public UpdateAccArguments setRevisionTrackingNum(Integer revisionTrackingNum) {
        this.revisionTrackingNum = revisionTrackingNum;
        return this;
    }

    public Integer getRevisionAction() {
        return revisionAction;
    }

    public UpdateAccArguments setRevisionAction(Integer revisionAction) {
        this.revisionAction = revisionAction;
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

    public ULong getNextAccId() {
        return nextAccId;
    }

    public UpdateAccArguments setNextAccId(ULong nextAccId) {
        this.nextAccId = nextAccId;
        return this;
    }

    public ULong execute(DSLContext dslContext) {
        return updateAcc(dslContext, this);
    }

    private ULong updateAcc(DSLContext dslContext, UpdateAccArguments arguments) {
        return dslContext.update(ACC)
                .set(ACC.OBJECT_CLASS_TERM, arguments.getObjectClassTerm())
                .set(ACC.DEN, arguments.getDen())
                .set(ACC.DEFINITION, arguments.getDefinition())
                .set(ACC.DEFINITION_SOURCE, arguments.getDefinitionSource())
                .set(ACC.OBJECT_CLASS_QUALIFIER, arguments.getObjectClassQualifier())
                .set(ACC.OAGIS_COMPONENT_TYPE, arguments.getOagisComponentType().getValue())
                .set(ACC.NAMESPACE_ID, arguments.getNamespaceId())
                .set(ACC.OWNER_USER_ID, arguments.getOwnerUserId())
                .set(ACC.LAST_UPDATED_BY, arguments.getLastUpdatedBy())
                .set(ACC.LAST_UPDATE_TIMESTAMP, arguments.getLastUpdateTimestamp())
                .set(ACC.STATE, arguments.getState().name())
                .set(ACC.REVISION_NUM, arguments.getRevisionNum())
                .set(ACC.REVISION_TRACKING_NUM, arguments.getRevisionTrackingNum())
                .set(ACC.REVISION_ACTION, arguments.getRevisionAction().byteValue())
                .set(ACC.IS_DEPRECATED, arguments.getDeprecated() ? (byte) 1: 0)
                .set(ACC.IS_ABSTRACT, arguments.getAbstract() ? (byte) 1: 0)
                .set(ACC.PREV_ACC_ID, arguments.getPrevAccId())
                .set(ACC.NEXT_ACC_ID, arguments.getNextAccId())
                .where(ACC.ACC_ID.eq(arguments.getAccId()))
                .returning(ACC.ACC_ID).fetchOne().getAccId();
    }
}
