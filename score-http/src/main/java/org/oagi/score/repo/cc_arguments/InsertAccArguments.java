package org.oagi.score.repo.cc_arguments;

import org.jooq.types.ULong;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.CoreComponentRepository;

import java.time.LocalDateTime;

public class InsertAccArguments {

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
    private ULong logId;
    private Boolean isDeprecated;
    private Boolean isAbstract;
    private ULong prevAccId;
    private ULong nextAccId;

    public InsertAccArguments(CoreComponentRepository repository) {
        this.repository = repository;
    }

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

    public ULong getRevisionId() {
        return logId;
    }

    public InsertAccArguments setRevisionId(ULong logId) {
        this.logId = logId;
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

    public ULong execute() {
        return repository.execute(this);
    }
}
