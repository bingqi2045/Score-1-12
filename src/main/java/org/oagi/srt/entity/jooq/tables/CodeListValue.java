/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.CodeListValueRecord;


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
public class CodeListValue extends TableImpl<CodeListValueRecord> {

    private static final long serialVersionUID = 787990779;

    /**
     * The reference instance of <code>oagi.code_list_value</code>
     */
    public static final CodeListValue CODE_LIST_VALUE = new CodeListValue();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CodeListValueRecord> getRecordType() {
        return CodeListValueRecord.class;
    }

    /**
     * The column <code>oagi.code_list_value.code_list_value_id</code>. Internal, primary database key.
     */
    public final TableField<CodeListValueRecord, ULong> CODE_LIST_VALUE_ID = createField(DSL.name("code_list_value_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Internal, primary database key.");

    /**
     * The column <code>oagi.code_list_value.code_list_id</code>. Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.
     */
    public final TableField<CodeListValueRecord, ULong> CODE_LIST_ID = createField(DSL.name("code_list_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.");

    /**
     * The column <code>oagi.code_list_value.value</code>. The code list value used in the instance data, e.g., EA, US-EN.
     */
    public final TableField<CodeListValueRecord, String> VALUE = createField(DSL.name("value"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "The code list value used in the instance data, e.g., EA, US-EN.");

    /**
     * The column <code>oagi.code_list_value.name</code>. Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.
     */
    public final TableField<CodeListValueRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "Pretty print name of the code list value, e.g., 'Each' for EA, 'English' for EN.");

    /**
     * The column <code>oagi.code_list_value.definition</code>. Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.
     */
    public final TableField<CodeListValueRecord, String> DEFINITION = createField(DSL.name("definition"), org.jooq.impl.SQLDataType.CLOB, this, "Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.");

    /**
     * The column <code>oagi.code_list_value.definition_source</code>. This is typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<CodeListValueRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), org.jooq.impl.SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.code_list_value.used_indicator</code>. This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.
     */
    public final TableField<CodeListValueRecord, Byte> USED_INDICATOR = createField(DSL.name("used_indicator"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates whether the code value is allowed to be used or not in that code list context. In other words, this flag allows a user to enable or disable a code list value.");

    /**
     * The column <code>oagi.code_list_value.locked_indicator</code>. This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.
     */
    public final TableField<CodeListValueRecord, Byte> LOCKED_INDICATOR = createField(DSL.name("locked_indicator"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates whether the USED_INDICATOR can be changed from False to True. In other words, if the code value is derived from its base code list and the USED_INDICATOR of the code value in the base is False, then the USED_iNDICATOR cannot be changed from False to True for this code value; and this is indicated using this LOCKED_INDICATOR flag in the derived code list.");

    /**
     * The column <code>oagi.code_list_value.extension_Indicator</code>. This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.
     */
    public final TableField<CodeListValueRecord, Byte> EXTENSION_INDICATOR = createField(DSL.name("extension_Indicator"), org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "This indicates whether this code value has just been added in this code list. It is used particularly in the derived code list. If the code value has only been added to the derived code list, then it can be deleted; otherwise, it cannot be deleted.");

    /**
     * Create a <code>oagi.code_list_value</code> table reference
     */
    public CodeListValue() {
        this(DSL.name("code_list_value"), null);
    }

    /**
     * Create an aliased <code>oagi.code_list_value</code> table reference
     */
    public CodeListValue(String alias) {
        this(DSL.name(alias), CODE_LIST_VALUE);
    }

    /**
     * Create an aliased <code>oagi.code_list_value</code> table reference
     */
    public CodeListValue(Name alias) {
        this(alias, CODE_LIST_VALUE);
    }

    private CodeListValue(Name alias, Table<CodeListValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private CodeListValue(Name alias, Table<CodeListValueRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Each record in this table stores a code list value of a code list. A code list value may be inherited from another code list on which it is based. However, inherited value may be restricted (i.e., disabled and cannot be used) in this code list, i.e., the USED_INDICATOR = false. If the value cannot be used since the based code list, then the LOCKED_INDICATOR = TRUE, because the USED_INDICATOR of such code list value is FALSE by default and can no longer be changed."), TableOptions.table());
    }

    public <O extends Record> CodeListValue(Table<O> child, ForeignKey<O, CodeListValueRecord> key) {
        super(child, key, CODE_LIST_VALUE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<CodeListValueRecord, ULong> getIdentity() {
        return Keys.IDENTITY_CODE_LIST_VALUE;
    }

    @Override
    public UniqueKey<CodeListValueRecord> getPrimaryKey() {
        return Keys.KEY_CODE_LIST_VALUE_PRIMARY;
    }

    @Override
    public List<UniqueKey<CodeListValueRecord>> getKeys() {
        return Arrays.<UniqueKey<CodeListValueRecord>>asList(Keys.KEY_CODE_LIST_VALUE_PRIMARY);
    }

    @Override
    public List<ForeignKey<CodeListValueRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CodeListValueRecord, ?>>asList(Keys.CODE_LIST_VALUE_CODE_LIST_ID_FK);
    }

    public CodeList codeList() {
        return new CodeList(this, Keys.CODE_LIST_VALUE_CODE_LIST_ID_FK);
    }

    @Override
    public CodeListValue as(String alias) {
        return new CodeListValue(DSL.name(alias), this);
    }

    @Override
    public CodeListValue as(Name alias) {
        return new CodeListValue(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeListValue rename(String name) {
        return new CodeListValue(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeListValue rename(Name name) {
        return new CodeListValue(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, ULong, String, String, String, String, Byte, Byte, Byte> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
