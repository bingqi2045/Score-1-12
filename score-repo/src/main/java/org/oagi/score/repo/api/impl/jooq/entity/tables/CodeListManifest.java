/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CodeListManifestRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CodeListManifest extends TableImpl<CodeListManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.code_list_manifest</code>
     */
    public static final CodeListManifest CODE_LIST_MANIFEST = new CodeListManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CodeListManifestRecord> getRecordType() {
        return CodeListManifestRecord.class;
    }

    /**
     * The column <code>oagi.code_list_manifest.code_list_manifest_id</code>.
     * Primary, internal database key.
     */
    public final TableField<CodeListManifestRecord, String> CODE_LIST_MANIFEST_ID = createField(DSL.name("code_list_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.code_list_manifest.release_id</code>. Foreign key
     * to the RELEASE table.
     */
    public final TableField<CodeListManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.code_list_manifest.code_list_id</code>.
     */
    public final TableField<CodeListManifestRecord, String> CODE_LIST_ID = createField(DSL.name("code_list_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column
     * <code>oagi.code_list_manifest.based_code_list_manifest_id</code>.
     */
    public final TableField<CodeListManifestRecord, String> BASED_CODE_LIST_MANIFEST_ID = createField(DSL.name("based_code_list_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column
     * <code>oagi.code_list_manifest.agency_id_list_value_manifest_id</code>.
     */
    public final TableField<CodeListManifestRecord, String> AGENCY_ID_LIST_VALUE_MANIFEST_ID = createField(DSL.name("agency_id_list_value_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.code_list_manifest.conflict</code>. This indicates
     * that there is a conflict between self and relationship.
     */
    public final TableField<CodeListManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.code_list_manifest.log_id</code>. A foreign key
     * pointed to a log for the current record.
     */
    public final TableField<CodeListManifestRecord, String> LOG_ID = createField(DSL.name("log_id"), SQLDataType.CHAR(36), this, "A foreign key pointed to a log for the current record.");

    /**
     * The column
     * <code>oagi.code_list_manifest.replacement_code_list_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<CodeListManifestRecord, String> REPLACEMENT_CODE_LIST_MANIFEST_ID = createField(DSL.name("replacement_code_list_manifest_id"), SQLDataType.CHAR(36), this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column
     * <code>oagi.code_list_manifest.prev_code_list_manifest_id</code>.
     */
    public final TableField<CodeListManifestRecord, String> PREV_CODE_LIST_MANIFEST_ID = createField(DSL.name("prev_code_list_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column
     * <code>oagi.code_list_manifest.next_code_list_manifest_id</code>.
     */
    public final TableField<CodeListManifestRecord, String> NEXT_CODE_LIST_MANIFEST_ID = createField(DSL.name("next_code_list_manifest_id"), SQLDataType.CHAR(36), this, "");

    private CodeListManifest(Name alias, Table<CodeListManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private CodeListManifest(Name alias, Table<CodeListManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.code_list_manifest</code> table reference
     */
    public CodeListManifest(String alias) {
        this(DSL.name(alias), CODE_LIST_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.code_list_manifest</code> table reference
     */
    public CodeListManifest(Name alias) {
        this(alias, CODE_LIST_MANIFEST);
    }

    /**
     * Create a <code>oagi.code_list_manifest</code> table reference
     */
    public CodeListManifest() {
        this(DSL.name("code_list_manifest"), null);
    }

    public <O extends Record> CodeListManifest(Table<O> child, ForeignKey<O, CodeListManifestRecord> key) {
        super(child, key, CODE_LIST_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<CodeListManifestRecord> getPrimaryKey() {
        return Keys.KEY_CODE_LIST_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<CodeListManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CODE_LIST_MANIFEST_RELEASE_ID_FK, Keys.CODE_LIST_MANIFEST_CODE_LIST_ID_FK, Keys.CODE_LIST_MANIFEST_BASED_CODE_LIST_MANIFEST_ID_FK, Keys.CODE_LIST_MANIFEST_AGENCY_ID_LIST_VALUE_MANIFEST_ID_FK, Keys.CODE_LIST_MANIFEST_LOG_ID_FK, Keys.CODE_LIST_MANIFEST_REPLACEMENT_CODE_LIST_MANIFEST_ID_FK, Keys.CODE_LIST_MANIFEST_PREV_CODE_LIST_MANIFEST_ID_FK, Keys.CODE_LIST_MANIFEST_NEXT_CODE_LIST_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient CodeList _codeList;
    private transient CodeListManifest _codeListManifestBasedCodeListManifestIdFk;
    private transient AgencyIdListValueManifest _agencyIdListValueManifest;
    private transient Log _log;
    private transient CodeListManifest _codeListManifestReplacementCodeListManifestIdFk;
    private transient CodeListManifest _codeListManifestPrevCodeListManifestIdFk;
    private transient CodeListManifest _codeListManifestNextCodeListManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.CODE_LIST_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list</code> table.
     */
    public CodeList codeList() {
        if (_codeList == null)
            _codeList = new CodeList(this, Keys.CODE_LIST_MANIFEST_CODE_LIST_ID_FK);

        return _codeList;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_manifest</code>
     * table, via the
     * <code>code_list_manifest_based_code_list_manifest_id_fk</code> key.
     */
    public CodeListManifest codeListManifestBasedCodeListManifestIdFk() {
        if (_codeListManifestBasedCodeListManifestIdFk == null)
            _codeListManifestBasedCodeListManifestIdFk = new CodeListManifest(this, Keys.CODE_LIST_MANIFEST_BASED_CODE_LIST_MANIFEST_ID_FK);

        return _codeListManifestBasedCodeListManifestIdFk;
    }

    /**
     * Get the implicit join path to the
     * <code>oagi.agency_id_list_value_manifest</code> table.
     */
    public AgencyIdListValueManifest agencyIdListValueManifest() {
        if (_agencyIdListValueManifest == null)
            _agencyIdListValueManifest = new AgencyIdListValueManifest(this, Keys.CODE_LIST_MANIFEST_AGENCY_ID_LIST_VALUE_MANIFEST_ID_FK);

        return _agencyIdListValueManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table.
     */
    public Log log() {
        if (_log == null)
            _log = new Log(this, Keys.CODE_LIST_MANIFEST_LOG_ID_FK);

        return _log;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_manifest</code>
     * table, via the
     * <code>code_list_manifest_replacement_code_list_manifest_id_fk</code> key.
     */
    public CodeListManifest codeListManifestReplacementCodeListManifestIdFk() {
        if (_codeListManifestReplacementCodeListManifestIdFk == null)
            _codeListManifestReplacementCodeListManifestIdFk = new CodeListManifest(this, Keys.CODE_LIST_MANIFEST_REPLACEMENT_CODE_LIST_MANIFEST_ID_FK);

        return _codeListManifestReplacementCodeListManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_manifest</code>
     * table, via the
     * <code>code_list_manifest_prev_code_list_manifest_id_fk</code> key.
     */
    public CodeListManifest codeListManifestPrevCodeListManifestIdFk() {
        if (_codeListManifestPrevCodeListManifestIdFk == null)
            _codeListManifestPrevCodeListManifestIdFk = new CodeListManifest(this, Keys.CODE_LIST_MANIFEST_PREV_CODE_LIST_MANIFEST_ID_FK);

        return _codeListManifestPrevCodeListManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.code_list_manifest</code>
     * table, via the
     * <code>code_list_manifest_next_code_list_manifest_id_fk</code> key.
     */
    public CodeListManifest codeListManifestNextCodeListManifestIdFk() {
        if (_codeListManifestNextCodeListManifestIdFk == null)
            _codeListManifestNextCodeListManifestIdFk = new CodeListManifest(this, Keys.CODE_LIST_MANIFEST_NEXT_CODE_LIST_MANIFEST_ID_FK);

        return _codeListManifestNextCodeListManifestIdFk;
    }

    @Override
    public CodeListManifest as(String alias) {
        return new CodeListManifest(DSL.name(alias), this);
    }

    @Override
    public CodeListManifest as(Name alias) {
        return new CodeListManifest(alias, this);
    }

    @Override
    public CodeListManifest as(Table<?> alias) {
        return new CodeListManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeListManifest rename(String name) {
        return new CodeListManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeListManifest rename(Name name) {
        return new CodeListManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeListManifest rename(Table<?> name) {
        return new CodeListManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, Byte, String, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
