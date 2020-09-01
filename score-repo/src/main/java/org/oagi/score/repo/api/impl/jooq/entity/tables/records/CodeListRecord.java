/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record20;
import org.jooq.Row20;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.CodeList;


/**
 * This table stores information about a code list. When a code list is derived 
 * from another code list, the whole set of code values belonging to the based 
 * code list will be copied.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListRecord extends UpdatableRecordImpl<CodeListRecord> implements Record20<ULong, String, String, String, String, ULong, String, String, String, String, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> {

    private static final long serialVersionUID = -1362206704;

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
     * Setter for <code>oagi.code_list.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
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
     * Setter for <code>oagi.code_list.extensible_indicator</code>. This is a flag to indicate whether the code list is final and shall not be further derived.
     */
    public void setExtensibleIndicator(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.code_list.extensible_indicator</code>. This is a flag to indicate whether the code list is final and shall not be further derived.
     */
    public Byte getExtensibleIndicator() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>oagi.code_list.is_deprecated</code>. Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public void setIsDeprecated(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.code_list.is_deprecated</code>. Indicates whether the code list is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>oagi.code_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public void setCreatedBy(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.code_list.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public ULong getCreatedBy() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.code_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.code_list.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.code_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public void setLastUpdatedBy(ULong value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.code_list.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(14);
    }

    /**
     * Setter for <code>oagi.code_list.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.code_list.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>oagi.code_list.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.code_list.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(16);
    }

    /**
     * Setter for <code>oagi.code_list.state</code>.
     */
    public void setState(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.code_list.state</code>.
     */
    public String getState() {
        return (String) get(17);
    }

    /**
     * Setter for <code>oagi.code_list.prev_code_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevCodeListId(ULong value) {
        set(18, value);
    }

    /**
     * Getter for <code>oagi.code_list.prev_code_list_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevCodeListId() {
        return (ULong) get(18);
    }

    /**
     * Setter for <code>oagi.code_list.next_code_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextCodeListId(ULong value) {
        set(19, value);
    }

    /**
     * Getter for <code>oagi.code_list.next_code_list_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextCodeListId() {
        return (ULong) get(19);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record20 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row20<ULong, String, String, String, String, ULong, String, String, String, String, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> fieldsRow() {
        return (Row20) super.fieldsRow();
    }

    @Override
    public Row20<ULong, String, String, String, String, ULong, String, String, String, String, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, String, ULong, ULong> valuesRow() {
        return (Row20) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeList.CODE_LIST.CODE_LIST_ID;
    }

    @Override
    public Field<String> field2() {
        return CodeList.CODE_LIST.GUID;
    }

    @Override
    public Field<String> field3() {
        return CodeList.CODE_LIST.ENUM_TYPE_GUID;
    }

    @Override
    public Field<String> field4() {
        return CodeList.CODE_LIST.NAME;
    }

    @Override
    public Field<String> field5() {
        return CodeList.CODE_LIST.LIST_ID;
    }

    @Override
    public Field<ULong> field6() {
        return CodeList.CODE_LIST.AGENCY_ID;
    }

    @Override
    public Field<String> field7() {
        return CodeList.CODE_LIST.VERSION_ID;
    }

    @Override
    public Field<String> field8() {
        return CodeList.CODE_LIST.DEFINITION;
    }

    @Override
    public Field<String> field9() {
        return CodeList.CODE_LIST.REMARK;
    }

    @Override
    public Field<String> field10() {
        return CodeList.CODE_LIST.DEFINITION_SOURCE;
    }

    @Override
    public Field<Byte> field11() {
        return CodeList.CODE_LIST.EXTENSIBLE_INDICATOR;
    }

    @Override
    public Field<Byte> field12() {
        return CodeList.CODE_LIST.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field13() {
        return CodeList.CODE_LIST.CREATED_BY;
    }

    @Override
    public Field<ULong> field14() {
        return CodeList.CODE_LIST.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field15() {
        return CodeList.CODE_LIST.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return CodeList.CODE_LIST.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field17() {
        return CodeList.CODE_LIST.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<String> field18() {
        return CodeList.CODE_LIST.STATE;
    }

    @Override
    public Field<ULong> field19() {
        return CodeList.CODE_LIST.PREV_CODE_LIST_ID;
    }

    @Override
    public Field<ULong> field20() {
        return CodeList.CODE_LIST.NEXT_CODE_LIST_ID;
    }

    @Override
    public ULong component1() {
        return getCodeListId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getEnumTypeGuid();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getListId();
    }

    @Override
    public ULong component6() {
        return getAgencyId();
    }

    @Override
    public String component7() {
        return getVersionId();
    }

    @Override
    public String component8() {
        return getDefinition();
    }

    @Override
    public String component9() {
        return getRemark();
    }

    @Override
    public String component10() {
        return getDefinitionSource();
    }

    @Override
    public Byte component11() {
        return getExtensibleIndicator();
    }

    @Override
    public Byte component12() {
        return getIsDeprecated();
    }

    @Override
    public ULong component13() {
        return getCreatedBy();
    }

    @Override
    public ULong component14() {
        return getOwnerUserId();
    }

    @Override
    public ULong component15() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component16() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component17() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String component18() {
        return getState();
    }

    @Override
    public ULong component19() {
        return getPrevCodeListId();
    }

    @Override
    public ULong component20() {
        return getNextCodeListId();
    }

    @Override
    public ULong value1() {
        return getCodeListId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getEnumTypeGuid();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getListId();
    }

    @Override
    public ULong value6() {
        return getAgencyId();
    }

    @Override
    public String value7() {
        return getVersionId();
    }

    @Override
    public String value8() {
        return getDefinition();
    }

    @Override
    public String value9() {
        return getRemark();
    }

    @Override
    public String value10() {
        return getDefinitionSource();
    }

    @Override
    public Byte value11() {
        return getExtensibleIndicator();
    }

    @Override
    public Byte value12() {
        return getIsDeprecated();
    }

    @Override
    public ULong value13() {
        return getCreatedBy();
    }

    @Override
    public ULong value14() {
        return getOwnerUserId();
    }

    @Override
    public ULong value15() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value16() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value17() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value18() {
        return getState();
    }

    @Override
    public ULong value19() {
        return getPrevCodeListId();
    }

    @Override
    public ULong value20() {
        return getNextCodeListId();
    }

    @Override
    public CodeListRecord value1(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CodeListRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public CodeListRecord value3(String value) {
        setEnumTypeGuid(value);
        return this;
    }

    @Override
    public CodeListRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public CodeListRecord value5(String value) {
        setListId(value);
        return this;
    }

    @Override
    public CodeListRecord value6(ULong value) {
        setAgencyId(value);
        return this;
    }

    @Override
    public CodeListRecord value7(String value) {
        setVersionId(value);
        return this;
    }

    @Override
    public CodeListRecord value8(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public CodeListRecord value9(String value) {
        setRemark(value);
        return this;
    }

    @Override
    public CodeListRecord value10(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public CodeListRecord value11(Byte value) {
        setExtensibleIndicator(value);
        return this;
    }

    @Override
    public CodeListRecord value12(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public CodeListRecord value13(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public CodeListRecord value14(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public CodeListRecord value15(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public CodeListRecord value16(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public CodeListRecord value17(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public CodeListRecord value18(String value) {
        setState(value);
        return this;
    }

    @Override
    public CodeListRecord value19(ULong value) {
        setPrevCodeListId(value);
        return this;
    }

    @Override
    public CodeListRecord value20(ULong value) {
        setNextCodeListId(value);
        return this;
    }

    @Override
    public CodeListRecord values(ULong value1, String value2, String value3, String value4, String value5, ULong value6, String value7, String value8, String value9, String value10, Byte value11, Byte value12, ULong value13, ULong value14, ULong value15, LocalDateTime value16, LocalDateTime value17, String value18, ULong value19, ULong value20) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        value20(value20);
        return this;
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
    public CodeListRecord(ULong codeListId, String guid, String enumTypeGuid, String name, String listId, ULong agencyId, String versionId, String definition, String remark, String definitionSource, Byte extensibleIndicator, Byte isDeprecated, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, String state, ULong prevCodeListId, ULong nextCodeListId) {
        super(CodeList.CODE_LIST);

        set(0, codeListId);
        set(1, guid);
        set(2, enumTypeGuid);
        set(3, name);
        set(4, listId);
        set(5, agencyId);
        set(6, versionId);
        set(7, definition);
        set(8, remark);
        set(9, definitionSource);
        set(10, extensibleIndicator);
        set(11, isDeprecated);
        set(12, createdBy);
        set(13, ownerUserId);
        set(14, lastUpdatedBy);
        set(15, creationTimestamp);
        set(16, lastUpdateTimestamp);
        set(17, state);
        set(18, prevCodeListId);
        set(19, nextCodeListId);
    }
}