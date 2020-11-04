/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeList;


/**
 * This table stores information about a code list. When a code list is derived 
 * from another code list, the whole set of code values belonging to the based 
 * code list will be copied.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListRecord extends UpdatableRecordImpl<CodeListRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.code_list.code_list_id</code>. Internal, primary database key.
     */
    public void setCodeListId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.code_list.code_list_id</code>. Internal, primary database key.
     */
    public ULong getCodeListId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.code_list.guid</code>. A globally unique identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list.guid</code>. A globally unique identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.code_list.enum_type_guid</code>. In the OAGIS Model XML schema, a type, which keeps all the enumerated values, is  defined separately from the type that represents a code list. This only applies to some code lists. When that is the case, this column stores the GUID of that enumeration type.
     */
    public void setEnumTypeGuid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list.enum_type_guid</code>. In the OAGIS Model XML schema, a type, which keeps all the enumerated values, is  defined separately from the type that represents a code list. This only applies to some code lists. When that is the case, this column stores the GUID of that enumeration type.
     */
    public String getEnumTypeGuid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.code_list.name</code>. Name of the code list.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.code_list.name</code>. Name of the code list.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.code_list.list_id</code>. External identifier.
     */
    public void setListId(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.code_list.list_id</code>. External identifier.
     */
    public String getListId() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.code_list.agency_id</code>. Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.
     */
    public void setAgencyId(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list.agency_id</code>. Foreign key to the AGENCY_ID_LIST_VALUE table. It indicates the organization which maintains the code list.
     */
    public ULong getAgencyId() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.code_list.version_id</code>. Code list version number.
     */
    public void setVersionId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.code_list.version_id</code>. Code list version number.
     */
    public String getVersionId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.code_list.definition</code>. Description of the code list.
     */
    public void setDefinition(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.code_list.definition</code>. Description of the code list.
     */
    public String getDefinition() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.code_list.remark</code>. Usage information about the code list.
     */
    public void setRemark(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.code_list.remark</code>. Usage information about the code list.
     */
    public String getRemark() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.code_list.definition_source</code>. This is typically a URL which indicates the source of the code list's DEFINITION.
     */
    public void setDefinitionSource(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.code_list.definition_source</code>. This is typically a URL which indicates the source of the code list's DEFINITION.
     */
    public String getDefinitionSource() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.code_list.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public void setNamespaceId(ULong value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.code_list.namespace_id</code>. Foreign key to the NAMESPACE table. This is the namespace to which the entity belongs. This namespace column is primarily used in the case the component is a user's component because there is also a namespace assigned at the release level.
     */
    public ULong getNamespaceId() {
        return (ULong) get(10);
    }

    /**
     * Setter for <code>oagi.code_list.based_code_list_id</code>. This is a foreign key to the CODE_LIST table itself. This identifies the code list on which this code list is based, if any. The derivation may be restriction and/or extension.
     */
    public void setBasedCodeListId(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.code_list.based_code_list_id</code>. This is a foreign key to the CODE_LIST table itself. This identifies the code list on which this code list is based, if any. The derivation may be restriction and/or extension.
     */
    public ULong getBasedCodeListId() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.code_list.extensible_indicator</code>. This is a flag to indicate whether the code list is final and shall not be further derived.
     */
    public void setExtensibleIndicator(Byte value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.code_list.extensible_indicator</code>. This is a flag to indicate whether the code list is final and shall not be further derived.
     */
    public Byte getExtensibleIndicator() {
        return (Byte) get(12);
    }

    /**
     * Setter for <code>oagi.code_list.is_deprecated</code>. Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public void setIsDeprecated(Byte value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.code_list.is_deprecated</code>. Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(13);
    }

    /**
     * Setter for <code>oagi.code_list.replacement_code_list_id</code>. This refers to a replacement if the record is deprecated.
     */
    public void setReplacementCodeListId(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.code_list.replacement_code_list_id</code>. This refers to a replacement if the record is deprecated.
     */
    public ULong getReplacementCodeListId() {
        return (ULong) get(14);
    }

    /**
     * Setter for <code>oagi.code_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public void setCreatedBy(ULong value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.code_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public ULong getCreatedBy() {
        return (ULong) get(15);
    }

    /**
     * Setter for <code>oagi.code_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.code_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(16);
    }

    /**
     * Setter for <code>oagi.code_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public void setLastUpdatedBy(ULong value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.code_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(17);
    }

    /**
     * Setter for <code>oagi.code_list.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.code_list.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(18);
    }

    /**
     * Setter for <code>oagi.code_list.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.code_list.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(19);
    }

    /**
     * Setter for <code>oagi.code_list.state</code>.
     */
    public void setState(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>oagi.code_list.state</code>.
     */
    public String getState() {
        return (String) get(20);
    }

    /**
     * Setter for <code>oagi.code_list.prev_code_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevCodeListId(ULong value) {
        set(21, value);
    }

    /**
     * Getter for <code>oagi.code_list.prev_code_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevCodeListId() {
        return (ULong) get(21);
    }

    /**
     * Setter for <code>oagi.code_list.next_code_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextCodeListId(ULong value) {
        set(22, value);
    }

    /**
     * Getter for <code>oagi.code_list.next_code_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextCodeListId() {
        return (ULong) get(22);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeListRecord
     */
    public CodeListRecord() {
        super(CodeList.CODE_LIST);
    }

    /**
     * Create a detached, initialised CodeListRecord
     */
    public CodeListRecord(ULong codeListId, String guid, String enumTypeGuid, String name, String listId, ULong agencyId, String versionId, String definition, String remark, String definitionSource, ULong namespaceId, ULong basedCodeListId, Byte extensibleIndicator, Byte isDeprecated, ULong replacementCodeListId, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, ULong prevCodeListId, ULong nextCodeListId) {
        super(CodeList.CODE_LIST);

        setCodeListId(codeListId);
        setGuid(guid);
        setEnumTypeGuid(enumTypeGuid);
        setName(name);
        setListId(listId);
        setAgencyId(agencyId);
        setVersionId(versionId);
        setDefinition(definition);
        setRemark(remark);
        setDefinitionSource(definitionSource);
        setNamespaceId(namespaceId);
        setBasedCodeListId(basedCodeListId);
        setExtensibleIndicator(extensibleIndicator);
        setIsDeprecated(isDeprecated);
        setReplacementCodeListId(replacementCodeListId);
        setCreatedBy(createdBy);
        setOwnerUserId(ownerUserId);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
        setState(state);
        setPrevCodeListId(prevCodeListId);
        setNextCodeListId(nextCodeListId);
    }
}
