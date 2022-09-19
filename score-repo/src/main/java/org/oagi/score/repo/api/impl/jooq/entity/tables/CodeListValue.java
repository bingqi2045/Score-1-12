/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function17;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row17;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CodeListValueRecord;


/**
 * Each record in this table stores a code list value of a code list. A code
 * list value may be inherited from another code list on which it is based.
 * However, inherited value may be restricted (i.e., disabled and cannot be
 * used) in this code list, i.e., the USED_INDICATOR = false. If the value
 * cannot be used since the based code list, then the LOCKED_INDICATOR = TRUE,
 * because the USED_INDICATOR of such code list value is FALSE by default and
 * can no longer be changed.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListValue extends TableImpl<CodeListValueRecord> {

    private static final long serialVersionUID = 1L;

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
     * The column <code>oagi.code_list_value.code_list_value_id</code>. Primary,
     * internal database key.
     */
    public final TableField<CodeListValueRecord, String> CODE_LIST_VALUE_ID = createField(DSL.name("code_list_value_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.code_list_value.guid</code>. A globally unique
     * identifier (GUID).
     */
    public final TableField<CodeListValueRecord, String> GUID = createField(DSL.name("guid"), SQLDataType.CHAR(32).nullable(false), this, "A globally unique identifier (GUID).");

    /**
     * The column <code>oagi.code_list_value.code_list_id</code>. Foreign key to
     * the CODE_LIST table. It indicates the code list this code value belonging
     * to.
     */
    public final TableField<CodeListValueRecord, String> CODE_LIST_ID = createField(DSL.name("code_list_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the CODE_LIST table. It indicates the code list this code value belonging to.");

    /**
     * The column <code>oagi.code_list_value.based_code_list_value_id</code>.
     * Foreign key to the CODE_LIST_VALUE table itself. This column is used when
     * the CODE_LIST is derived from the based CODE_LIST.
     */
    public final TableField<CodeListValueRecord, String> BASED_CODE_LIST_VALUE_ID = createField(DSL.name("based_code_list_value_id"), SQLDataType.CHAR(36), this, "Foreign key to the CODE_LIST_VALUE table itself. This column is used when the CODE_LIST is derived from the based CODE_LIST.");

    /**
     * The column <code>oagi.code_list_value.value</code>. The code list value
     * used in the instance data, e.g., EA, US-EN.
     */
    public final TableField<CodeListValueRecord, String> VALUE = createField(DSL.name("value"), SQLDataType.CLOB.nullable(false), this, "The code list value used in the instance data, e.g., EA, US-EN.");

    /**
     * The column <code>oagi.code_list_value.meaning</code>. The description or
     * explanation of the code list value, e.g., 'Each' for EA, 'English' for
     * EN.
     */
    public final TableField<CodeListValueRecord, String> MEANING = createField(DSL.name("meaning"), SQLDataType.VARCHAR(100), this, "The description or explanation of the code list value, e.g., 'Each' for EA, 'English' for EN.");

    /**
     * The column <code>oagi.code_list_value.definition</code>. Long description
     * or explannation of the code list value, e.g., 'EA is a discrete quantity
     * for counting each unit of an item, such as, 2 shampoo bottles, 3 box of
     * cereals'.
     */
    public final TableField<CodeListValueRecord, String> DEFINITION = createField(DSL.name("definition"), SQLDataType.CLOB, this, "Long description or explannation of the code list value, e.g., 'EA is a discrete quantity for counting each unit of an item, such as, 2 shampoo bottles, 3 box of cereals'.");

    /**
     * The column <code>oagi.code_list_value.definition_source</code>. This is
     * typically a URL identifying the source of the DEFINITION column.
     */
    public final TableField<CodeListValueRecord, String> DEFINITION_SOURCE = createField(DSL.name("definition_source"), SQLDataType.VARCHAR(100), this, "This is typically a URL identifying the source of the DEFINITION column.");

    /**
     * The column <code>oagi.code_list_value.is_deprecated</code>. Indicates
     * whether the code list value is deprecated and should not be reused (i.e.,
     * no new reference to this record should be allowed).
     */
    public final TableField<CodeListValueRecord, Byte> IS_DEPRECATED = createField(DSL.name("is_deprecated"), SQLDataType.TINYINT.defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "Indicates whether the code list value is deprecated and should not be reused (i.e., no new reference to this record should be allowed).");

    /**
     * The column
     * <code>oagi.code_list_value.replacement_code_list_value_id</code>. This
     * refers to a replacement if the record is deprecated.
     */
    public final TableField<CodeListValueRecord, String> REPLACEMENT_CODE_LIST_VALUE_ID = createField(DSL.name("replacement_code_list_value_id"), SQLDataType.CHAR(36), this, "This refers to a replacement if the record is deprecated.");

    /**
     * The column <code>oagi.code_list_value.created_by</code>. Foreign key to
     * the APP_USER table. It indicates the user who created the code list
     * value.
     */
    public final TableField<CodeListValueRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created the code list value.");

    /**
     * The column <code>oagi.code_list_value.owner_user_id</code>. Foreign key
     * to the APP_USER table. This is the user who owns the entity, is allowed
     * to edit the entity, and who can transfer the ownership to another user.
     * 
     * The ownership can change throughout the history, but undoing shouldn't
     * rollback the ownership.
     */
    public final TableField<CodeListValueRecord, String> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. This is the user who owns the entity, is allowed to edit the entity, and who can transfer the ownership to another user.\n\nThe ownership can change throughout the history, but undoing shouldn't rollback the ownership.");

    /**
     * The column <code>oagi.code_list_value.last_updated_by</code>. Foreign key
     * to the APP_USER table. It identifies the user who last updated the code
     * list value.
     */
    public final TableField<CodeListValueRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It identifies the user who last updated the code list value.");

    /**
     * The column <code>oagi.code_list_value.creation_timestamp</code>.
     * Timestamp when the code list was created.
     */
    public final TableField<CodeListValueRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the code list was created.");

    /**
     * The column <code>oagi.code_list_value.last_update_timestamp</code>.
     * Timestamp when the code list was last updated.
     */
    public final TableField<CodeListValueRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP(6)", SQLDataType.LOCALDATETIME)), this, "Timestamp when the code list was last updated.");

    /**
     * The column <code>oagi.code_list_value.prev_code_list_value_id</code>. A
     * self-foreign key to indicate the previous history record.
     */
    public final TableField<CodeListValueRecord, String> PREV_CODE_LIST_VALUE_ID = createField(DSL.name("prev_code_list_value_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the previous history record.");

    /**
     * The column <code>oagi.code_list_value.next_code_list_value_id</code>. A
     * self-foreign key to indicate the next history record.
     */
    public final TableField<CodeListValueRecord, String> NEXT_CODE_LIST_VALUE_ID = createField(DSL.name("next_code_list_value_id"), SQLDataType.CHAR(36), this, "A self-foreign key to indicate the next history record.");

    private CodeListValue(Name alias, Table<CodeListValueRecord> aliased) {
        this(alias, aliased, null);
    }

    private CodeListValue(Name alias, Table<CodeListValueRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Each record in this table stores a code list value of a code list. A code list value may be inherited from another code list on which it is based. However, inherited value may be restricted (i.e., disabled and cannot be used) in this code list, i.e., the USED_INDICATOR = false. If the value cannot be used since the based code list, then the LOCKED_INDICATOR = TRUE, because the USED_INDICATOR of such code list value is FALSE by default and can no longer be changed."), TableOptions.table());
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

    /**
     * Create a <code>oagi.code_list_value</code> table reference
     */
    public CodeListValue() {
        this(DSL.name("code_list_value"), null);
    }

    public <O extends Record> CodeListValue(Table<O> child, ForeignKey<O, CodeListValueRecord> key) {
        super(child, key, CODE_LIST_VALUE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<CodeListValueRecord> getPrimaryKey() {
        return Keys.KEY_CODE_LIST_VALUE_PRIMARY;
    }

    @Override
    public List<ForeignKey<CodeListValueRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CODE_LIST_VALUE_CODE_LIST_ID_FK, Keys.CODE_LIST_VALUE_BASED_CODE_LIST_VALUE_ID_FK, Keys.CODE_LIST_VALUE_REPLACEMENT_CODE_LIST_VALUE_ID_FK, Keys.CODE_LIST_VALUE_CREATED_BY_FK, Keys.CODE_LIST_VALUE_OWNER_USER_ID_FK, Keys.CODE_LIST_VALUE_LAST_UPDATED_BY_FK, Keys.CODE_LIST_VALUE_PREV_CODE_LIST_VALUE_ID_FK, Keys.CODE_LIST_VALUE_NEXT_CODE_LIST_VALUE_ID_FK);
    }

    private transient CodeList _codeList;
    private transient CodeListValue _codeListValueBasedCodeListValueIdFk;
    private transient CodeListValue _codeListValueReplacementCodeListValueIdFk;
    private transient AppUser _codeListValueCreatedByFk;
    private transient AppUser _codeListValueOwnerUserIdFk;
    private transient AppUser _codeListValueLastUpdatedByFk;
    private transient CodeListValue _codeListValuePrevCodeListValueIdFk;
    private transient CodeListValue _codeListValueNextCodeListValueIdFk;

    /**
     * Get the implicit join path to the <code>oagi.code_list</code> table.
     */
    public CodeList codeList() {
        if (_codeList == null)
            _codeList = new CodeList(this, Keys.CODE_LIST_VALUE_CODE_LIST_ID_FK);

        return _codeList;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_value</code>
     * table, via the <code>code_list_value_based_code_list_value_id_fk</code>
     * key.
     */
    public CodeListValue codeListValueBasedCodeListValueIdFk() {
        if (_codeListValueBasedCodeListValueIdFk == null)
            _codeListValueBasedCodeListValueIdFk = new CodeListValue(this, Keys.CODE_LIST_VALUE_BASED_CODE_LIST_VALUE_ID_FK);

        return _codeListValueBasedCodeListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_value</code>
     * table, via the
     * <code>code_list_value_replacement_code_list_value_id_fk</code> key.
     */
    public CodeListValue codeListValueReplacementCodeListValueIdFk() {
        if (_codeListValueReplacementCodeListValueIdFk == null)
            _codeListValueReplacementCodeListValueIdFk = new CodeListValue(this, Keys.CODE_LIST_VALUE_REPLACEMENT_CODE_LIST_VALUE_ID_FK);

        return _codeListValueReplacementCodeListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>code_list_value_created_by_fk</code> key.
     */
    public AppUser codeListValueCreatedByFk() {
        if (_codeListValueCreatedByFk == null)
            _codeListValueCreatedByFk = new AppUser(this, Keys.CODE_LIST_VALUE_CREATED_BY_FK);

        return _codeListValueCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>code_list_value_owner_user_id_fk</code> key.
     */
    public AppUser codeListValueOwnerUserIdFk() {
        if (_codeListValueOwnerUserIdFk == null)
            _codeListValueOwnerUserIdFk = new AppUser(this, Keys.CODE_LIST_VALUE_OWNER_USER_ID_FK);

        return _codeListValueOwnerUserIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>code_list_value_last_updated_by_fk</code> key.
     */
    public AppUser codeListValueLastUpdatedByFk() {
        if (_codeListValueLastUpdatedByFk == null)
            _codeListValueLastUpdatedByFk = new AppUser(this, Keys.CODE_LIST_VALUE_LAST_UPDATED_BY_FK);

        return _codeListValueLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_value</code>
     * table, via the <code>code_list_value_prev_code_list_value_id_fk</code>
     * key.
     */
    public CodeListValue codeListValuePrevCodeListValueIdFk() {
        if (_codeListValuePrevCodeListValueIdFk == null)
            _codeListValuePrevCodeListValueIdFk = new CodeListValue(this, Keys.CODE_LIST_VALUE_PREV_CODE_LIST_VALUE_ID_FK);

        return _codeListValuePrevCodeListValueIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_value</code>
     * table, via the <code>code_list_value_next_code_list_value_id_fk</code>
     * key.
     */
    public CodeListValue codeListValueNextCodeListValueIdFk() {
        if (_codeListValueNextCodeListValueIdFk == null)
            _codeListValueNextCodeListValueIdFk = new CodeListValue(this, Keys.CODE_LIST_VALUE_NEXT_CODE_LIST_VALUE_ID_FK);

        return _codeListValueNextCodeListValueIdFk;
    }

    @Override
    public CodeListValue as(String alias) {
        return new CodeListValue(DSL.name(alias), this);
    }

    @Override
    public CodeListValue as(Name alias) {
        return new CodeListValue(alias, this);
    }

    @Override
    public CodeListValue as(Table<?> alias) {
        return new CodeListValue(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public CodeListValue rename(Table<?> name) {
        return new CodeListValue(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row17 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row17<String, String, String, String, String, String, String, String, Byte, String, String, String, String, LocalDateTime, LocalDateTime, String, String> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function17<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function17<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
