/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record18;
import org.jooq.Row18;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CodeListValue;

import java.time.LocalDateTime;


/**
 * Each record in this table stores a code list value of a code list. A code 
 * list value may be inherited from another code list on which it is based. 
 * However, inherited value may be restricted (i.e., disabled and cannot be 
 * used) in this code list, i.e., the USED_INDICATOR = false. If the value 
 * cannot be used since the based code list, then the LOCKED_INDICATOR = TRUE, 
 * because the USED_INDICATOR of such code list value is FALSE by default 
 * and can no longer be changed.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListValueRecord extends UpdatableRecordImpl<CodeListValueRecord> implements Record18<ULong, String, ULong, String, String, String, String, Byte, Byte, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> {

    private static final long serialVersionUID = 447668675;

    /**
     * Setter for <code>oagi.code_list_value.code_list_value_id</code>. Internal, primary database key.
     */
    public void setCodeListValueId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.code_list_value_id</code>. Internal, primary database key.
     */
    public ULong getCodeListValueId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.code_list_value.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.guid</code>. GUID of the code list. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_value.code_list_id</code>. Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.
     */
    public void setCodeListId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.code_list_id</code>. Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.
     */
    public ULong getCodeListId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.code_list_value.value</code>. The code list value used in the instance data, e.g., EA, US-EN.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.value</code>. The code list value used in the instance data, e.g., EA, US-EN.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.code_list_value.name</code>. Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.
     */
    public void setName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.name</code>. Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.
     */
    public String getName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.code_list_value.definition</code>. Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.
     */
    public void setDefinition(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.definition</code>. Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.
     */
    public String getDefinition() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.code_list_value.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.code_list_value.used_indicator</code>. This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.
     */
    public void setUsedIndicator(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.used_indicator</code>. This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.
     */
    public Byte getUsedIndicator() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>oagi.code_list_value.locked_indicator</code>. This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.
     */
    public void setLockedIndicator(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.locked_indicator</code>. This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.
     */
    public Byte getLockedIndicator() {
        return (Byte) get(8);
    }

    /**
     * Setter for <code>oagi.code_list_value.extension_Indicator</code>. This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.
     */
    public void setExtensionIndicator(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.extension_Indicator</code>. This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.
     */
    public Byte getExtensionIndicator() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>oagi.code_list_value.is_deprecated</code>. Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public void setIsDeprecated(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.is_deprecated</code>. Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).
     */
    public Byte getIsDeprecated() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>oagi.code_list_value.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public void setCreatedBy(ULong value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created the code list.
     */
    public ULong getCreatedBy() {
        return (ULong) get(11);
    }

    /**
     * Setter for <code>oagi.code_list_value.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public void setOwnerUserId(ULong value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.owner_user_id</code>. Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.

The ownership can change throughout the history, but undoing shouldn't rollback the ownership.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(12);
    }

    /**
     * Setter for <code>oagi.code_list_value.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public void setLastUpdatedBy(ULong value) {
        set(13, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.last_updated_by</code>. Foreign key to the APP_USER table. It identifies the user who last updated the code list.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(13);
    }

    /**
     * Setter for <code>oagi.code_list_value.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(14, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.creation_timestamp</code>. Timestamp when the code list was created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>oagi.code_list_value.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(15, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.last_update_timestamp</code>. Timestamp when the code list was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>oagi.code_list_value.prev_code_list_value_id</code>. A self-foreign key to indicate the previous history record.
     */
    public void setPrevCodeListValueId(ULong value) {
        set(16, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.prev_code_list_value_id</code>. A self-foreign key to indicate the previous history record.
     */
    public ULong getPrevCodeListValueId() {
        return (ULong) get(16);
    }

    /**
     * Setter for <code>oagi.code_list_value.next_code_list_value_id</code>. A self-foreign key to indicate the next history record.
     */
    public void setNextCodeListValueId(ULong value) {
        set(17, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.next_code_list_value_id</code>. A self-foreign key to indicate the next history record.
     */
    public ULong getNextCodeListValueId() {
        return (ULong) get(17);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record18 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row18<ULong, String, ULong, String, String, String, String, Byte, Byte, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> fieldsRow() {
        return (Row18) super.fieldsRow();
    }

    @Override
    public Row18<ULong, String, ULong, String, String, String, String, Byte, Byte, Byte, Byte, ULong, ULong, ULong, LocalDateTime, LocalDateTime, ULong, ULong> valuesRow() {
        return (Row18) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeListValue.CODE_LIST_VALUE.CODE_LIST_VALUE_ID;
    }

    @Override
    public Field<String> field2() {
        return CodeListValue.CODE_LIST_VALUE.GUID;
    }

    @Override
    public Field<ULong> field3() {
        return CodeListValue.CODE_LIST_VALUE.CODE_LIST_ID;
    }

    @Override
    public Field<String> field4() {
        return CodeListValue.CODE_LIST_VALUE.VALUE;
    }

    @Override
    public Field<String> field5() {
        return CodeListValue.CODE_LIST_VALUE.NAME;
    }

    @Override
    public Field<String> field6() {
        return CodeListValue.CODE_LIST_VALUE.DEFINITION;
    }

    @Override
    public Field<String> field7() {
        return CodeListValue.CODE_LIST_VALUE.DEFINITION_SOURCE;
    }

    @Override
    public Field<Byte> field8() {
        return CodeListValue.CODE_LIST_VALUE.USED_INDICATOR;
    }

    @Override
    public Field<Byte> field9() {
        return CodeListValue.CODE_LIST_VALUE.LOCKED_INDICATOR;
    }

    @Override
    public Field<Byte> field10() {
        return CodeListValue.CODE_LIST_VALUE.EXTENSION_INDICATOR;
    }

    @Override
    public Field<Byte> field11() {
        return CodeListValue.CODE_LIST_VALUE.IS_DEPRECATED;
    }

    @Override
    public Field<ULong> field12() {
        return CodeListValue.CODE_LIST_VALUE.CREATED_BY;
    }

    @Override
    public Field<ULong> field13() {
        return CodeListValue.CODE_LIST_VALUE.OWNER_USER_ID;
    }

    @Override
    public Field<ULong> field14() {
        return CodeListValue.CODE_LIST_VALUE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field15() {
        return CodeListValue.CODE_LIST_VALUE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field16() {
        return CodeListValue.CODE_LIST_VALUE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public Field<ULong> field17() {
        return CodeListValue.CODE_LIST_VALUE.PREV_CODE_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field18() {
        return CodeListValue.CODE_LIST_VALUE.NEXT_CODE_LIST_VALUE_ID;
    }

    @Override
    public ULong component1() {
        return getCodeListValueId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public ULong component3() {
        return getCodeListId();
    }

    @Override
    public String component4() {
        return getValue();
    }

    @Override
    public String component5() {
        return getName();
    }

    @Override
    public String component6() {
        return getDefinition();
    }

    @Override
    public String component7() {
        return getDefinitionSource();
    }

    @Override
    public Byte component8() {
        return getUsedIndicator();
    }

    @Override
    public Byte component9() {
        return getLockedIndicator();
    }

    @Override
    public Byte component10() {
        return getExtensionIndicator();
    }

    @Override
    public Byte component11() {
        return getIsDeprecated();
    }

    @Override
    public ULong component12() {
        return getCreatedBy();
    }

    @Override
    public ULong component13() {
        return getOwnerUserId();
    }

    @Override
    public ULong component14() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component15() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component16() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong component17() {
        return getPrevCodeListValueId();
    }

    @Override
    public ULong component18() {
        return getNextCodeListValueId();
    }

    @Override
    public ULong value1() {
        return getCodeListValueId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public ULong value3() {
        return getCodeListId();
    }

    @Override
    public String value4() {
        return getValue();
    }

    @Override
    public String value5() {
        return getName();
    }

    @Override
    public String value6() {
        return getDefinition();
    }

    @Override
    public String value7() {
        return getDefinitionSource();
    }

    @Override
    public Byte value8() {
        return getUsedIndicator();
    }

    @Override
    public Byte value9() {
        return getLockedIndicator();
    }

    @Override
    public Byte value10() {
        return getExtensionIndicator();
    }

    @Override
    public Byte value11() {
        return getIsDeprecated();
    }

    @Override
    public ULong value12() {
        return getCreatedBy();
    }

    @Override
    public ULong value13() {
        return getOwnerUserId();
    }

    @Override
    public ULong value14() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value15() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value16() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value17() {
        return getPrevCodeListValueId();
    }

    @Override
    public ULong value18() {
        return getNextCodeListValueId();
    }

    @Override
    public CodeListValueRecord value1(ULong value) {
        setCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public CodeListValueRecord value3(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value4(String value) {
        setValue(value);
        return this;
    }

    @Override
    public CodeListValueRecord value5(String value) {
        setName(value);
        return this;
    }

    @Override
    public CodeListValueRecord value6(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public CodeListValueRecord value7(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public CodeListValueRecord value8(Byte value) {
        setUsedIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord value9(Byte value) {
        setLockedIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord value10(Byte value) {
        setExtensionIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord value11(Byte value) {
        setIsDeprecated(value);
        return this;
    }

    @Override
    public CodeListValueRecord value12(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public CodeListValueRecord value13(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value14(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public CodeListValueRecord value15(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public CodeListValueRecord value16(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public CodeListValueRecord value17(ULong value) {
        setPrevCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value18(ULong value) {
        setNextCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueRecord values(ULong value1, String value2, ULong value3, String value4, String value5, String value6, String value7, Byte value8, Byte value9, Byte value10, Byte value11, ULong value12, ULong value13, ULong value14, LocalDateTime value15, LocalDateTime value16, ULong value17, ULong value18) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CodeListValueRecord
     */
    public CodeListValueRecord() {
        super(CodeListValue.CODE_LIST_VALUE);
    }

    /**
     * Create a detached, initialised CodeListValueRecord
     */
    public CodeListValueRecord(ULong codeListValueId, String guid, ULong codeListId, String value, String name, String definition, String definitionSource, Byte usedIndicator, Byte lockedIndicator, Byte extensionIndicator, Byte isDeprecated, ULong createdBy, ULong ownerUserId, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp, ULong prevCodeListValueId, ULong nextCodeListValueId) {
        super(CodeListValue.CODE_LIST_VALUE);

        set(0, codeListValueId);
        set(1, guid);
        set(2, codeListId);
        set(3, value);
        set(4, name);
        set(5, definition);
        set(6, definitionSource);
        set(7, usedIndicator);
        set(8, lockedIndicator);
        set(9, extensionIndicator);
        set(10, isDeprecated);
        set(11, createdBy);
        set(12, ownerUserId);
        set(13, lastUpdatedBy);
        set(14, creationTimestamp);
        set(15, lastUpdateTimestamp);
        set(16, prevCodeListValueId);
        set(17, nextCodeListValueId);
    }
}
