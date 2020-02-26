/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Row9;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.CodeListValue;


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
public class CodeListValueRecord extends UpdatableRecordImpl<CodeListValueRecord> implements Record9<ULong, ULong, String, String, String, String, Byte, Byte, Byte> {

    private static final long serialVersionUID = -364018479;

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
     * Setter for <code>oagi.code_list_value.code_list_id</code>. Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.
     */
    public void setCodeListId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.code_list_id</code>. Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.
     */
    public ULong getCodeListId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.code_list_value.value</code>. The code list value used in the instance data, e.g., EA, US-EN.
     */
    public void setValue(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.value</code>. The code list value used in the instance data, e.g., EA, US-EN.
     */
    public String getValue() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.code_list_value.name</code>. Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.name</code>. Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.code_list_value.definition</code>. Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.
     */
    public void setDefinition(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.definition</code>. Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.code_list_value.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public void setDefinitionSource(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public String getDefinitionSource() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.code_list_value.used_indicator</code>. This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.
     */
    public void setUsedIndicator(Byte value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.used_indicator</code>. This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.
     */
    public Byte getUsedIndicator() {
        return (Byte) get(6);
    }

    /**
     * Setter for <code>oagi.code_list_value.locked_indicator</code>. This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.
     */
    public void setLockedIndicator(Byte value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.locked_indicator</code>. This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.
     */
    public Byte getLockedIndicator() {
        return (Byte) get(7);
    }

    /**
     * Setter for <code>oagi.code_list_value.extension_Indicator</code>. This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.
     */
    public void setExtensionIndicator(Byte value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.code_list_value.extension_Indicator</code>. This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.
     */
    public Byte getExtensionIndicator() {
        return (Byte) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, ULong, String, String, String, String, Byte, Byte, Byte> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    @Override
    public Row9<ULong, ULong, String, String, String, String, Byte, Byte, Byte> valuesRow() {
        return (Row9) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return CodeListValue.CODE_LIST_VALUE.CODE_LIST_VALUE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return CodeListValue.CODE_LIST_VALUE.CODE_LIST_ID;
    }

    @Override
    public Field<String> field3() {
        return CodeListValue.CODE_LIST_VALUE.VALUE;
    }

    @Override
    public Field<String> field4() {
        return CodeListValue.CODE_LIST_VALUE.NAME;
    }

    @Override
    public Field<String> field5() {
        return CodeListValue.CODE_LIST_VALUE.DEFINITION;
    }

    @Override
    public Field<String> field6() {
        return CodeListValue.CODE_LIST_VALUE.DEFINITION_SOURCE;
    }

    @Override
    public Field<Byte> field7() {
        return CodeListValue.CODE_LIST_VALUE.USED_INDICATOR;
    }

    @Override
    public Field<Byte> field8() {
        return CodeListValue.CODE_LIST_VALUE.LOCKED_INDICATOR;
    }

    @Override
    public Field<Byte> field9() {
        return CodeListValue.CODE_LIST_VALUE.EXTENSION_INDICATOR;
    }

    @Override
    public ULong component1() {
        return getCodeListValueId();
    }

    @Override
    public ULong component2() {
        return getCodeListId();
    }

    @Override
    public String component3() {
        return getValue();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public String component5() {
        return getDefinition();
    }

    @Override
    public String component6() {
        return getDefinitionSource();
    }

    @Override
    public Byte component7() {
        return getUsedIndicator();
    }

    @Override
    public Byte component8() {
        return getLockedIndicator();
    }

    @Override
    public Byte component9() {
        return getExtensionIndicator();
    }

    @Override
    public ULong value1() {
        return getCodeListValueId();
    }

    @Override
    public ULong value2() {
        return getCodeListId();
    }

    @Override
    public String value3() {
        return getValue();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public String value5() {
        return getDefinition();
    }

    @Override
    public String value6() {
        return getDefinitionSource();
    }

    @Override
    public Byte value7() {
        return getUsedIndicator();
    }

    @Override
    public Byte value8() {
        return getLockedIndicator();
    }

    @Override
    public Byte value9() {
        return getExtensionIndicator();
    }

    @Override
    public CodeListValueRecord value1(ULong value) {
        setCodeListValueId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value2(ULong value) {
        setCodeListId(value);
        return this;
    }

    @Override
    public CodeListValueRecord value3(String value) {
        setValue(value);
        return this;
    }

    @Override
    public CodeListValueRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public CodeListValueRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    @Override
    public CodeListValueRecord value6(String value) {
        setDefinitionSource(value);
        return this;
    }

    @Override
    public CodeListValueRecord value7(Byte value) {
        setUsedIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord value8(Byte value) {
        setLockedIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord value9(Byte value) {
        setExtensionIndicator(value);
        return this;
    }

    @Override
    public CodeListValueRecord values(ULong value1, ULong value2, String value3, String value4, String value5, String value6, Byte value7, Byte value8, Byte value9) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
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
    public CodeListValueRecord(ULong codeListValueId, ULong codeListId, String value, String name, String definition, String definitionSource, Byte usedIndicator, Byte lockedIndicator, Byte extensionIndicator) {
        super(CodeListValue.CODE_LIST_VALUE);

        set(0, codeListValueId);
        set(1, codeListId);
        set(2, value);
        set(3, name);
        set(4, definition);
        set(5, definitionSource);
        set(6, usedIndicator);
        set(7, lockedIndicator);
        set(8, extensionIndicator);
    }
}
