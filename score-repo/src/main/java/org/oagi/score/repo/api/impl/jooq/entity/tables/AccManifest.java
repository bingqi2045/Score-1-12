/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function9;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row9;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccManifest extends TableImpl<AccManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.acc_manifest</code>
     */
    public static final AccManifest ACC_MANIFEST = new AccManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AccManifestRecord> getRecordType() {
        return AccManifestRecord.class;
    }

    /**
     * The column <code>oagi.acc_manifest.acc_manifest_id</code>. Primary,
     * internal database key.
     */
    public final TableField<AccManifestRecord, String> ACC_MANIFEST_ID = createField(DSL.name("acc_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.acc_manifest.release_id</code>. Foreign key to the
     * RELEASE table.
     */
    public final TableField<AccManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.acc_manifest.acc_id</code>.
     */
    public final TableField<AccManifestRecord, String> ACC_ID = createField(DSL.name("acc_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.acc_manifest.based_acc_manifest_id</code>.
     */
    public final TableField<AccManifestRecord, String> BASED_ACC_MANIFEST_ID = createField(DSL.name("based_acc_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.acc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<AccManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.acc_manifest.log_id</code>. A foreign key pointed
     * to a log for the current record.
     */
    public final TableField<AccManifestRecord, String> LOG_ID = createField(DSL.name("log_id"), SQLDataType.CHAR(36), this, "A foreign key pointed to a log for the current record.");

    /**
     * The column <code>oagi.acc_manifest.replacement_acc_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<AccManifestRecord, String> REPLACEMENT_ACC_MANIFEST_ID = createField(DSL.name("replacement_acc_manifest_id"), SQLDataType.CHAR(36), this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column <code>oagi.acc_manifest.prev_acc_manifest_id</code>.
     */
    public final TableField<AccManifestRecord, String> PREV_ACC_MANIFEST_ID = createField(DSL.name("prev_acc_manifest_id"), SQLDataType.CHAR(36), this, "");

    /**
     * The column <code>oagi.acc_manifest.next_acc_manifest_id</code>.
     */
    public final TableField<AccManifestRecord, String> NEXT_ACC_MANIFEST_ID = createField(DSL.name("next_acc_manifest_id"), SQLDataType.CHAR(36), this, "");

    private AccManifest(Name alias, Table<AccManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AccManifest(Name alias, Table<AccManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.acc_manifest</code> table reference
     */
    public AccManifest(String alias) {
        this(DSL.name(alias), ACC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.acc_manifest</code> table reference
     */
    public AccManifest(Name alias) {
        this(alias, ACC_MANIFEST);
    }

    /**
     * Create a <code>oagi.acc_manifest</code> table reference
     */
    public AccManifest() {
        this(DSL.name("acc_manifest"), null);
    }

    public <O extends Record> AccManifest(Table<O> child, ForeignKey<O, AccManifestRecord> key) {
        super(child, key, ACC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<AccManifestRecord> getPrimaryKey() {
        return Keys.KEY_ACC_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<AccManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ACC_MANIFEST_RELEASE_ID_FK, Keys.ACC_MANIFEST_ACC_ID_FK, Keys.ACC_MANIFEST_BASED_ACC_MANIFEST_ID_FK, Keys.ACC_MANIFEST_LOG_ID_FK, Keys.ACC_MANIFEST_REPLACEMENT_ACC_MANIFEST_ID_FK, Keys.ACC_MANIFEST_PREV_ACC_MANIFEST_ID_FK, Keys.ACC_MANIFEST_NEXT_ACC_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient Acc _acc;
    private transient AccManifest _accManifestBasedAccManifestIdFk;
    private transient Log _log;
    private transient AccManifest _accManifestReplacementAccManifestIdFk;
    private transient AccManifest _accManifestPrevAccManifestIdFk;
    private transient AccManifest _accManifestNextAccManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.ACC_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc</code> table.
     */
    public Acc acc() {
        if (_acc == null)
            _acc = new Acc(this, Keys.ACC_MANIFEST_ACC_ID_FK);

        return _acc;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table,
     * via the <code>acc_manifest_based_acc_manifest_id_fk</code> key.
     */
    public AccManifest accManifestBasedAccManifestIdFk() {
        if (_accManifestBasedAccManifestIdFk == null)
            _accManifestBasedAccManifestIdFk = new AccManifest(this, Keys.ACC_MANIFEST_BASED_ACC_MANIFEST_ID_FK);

        return _accManifestBasedAccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table.
     */
    public Log log() {
        if (_log == null)
            _log = new Log(this, Keys.ACC_MANIFEST_LOG_ID_FK);

        return _log;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table,
     * via the <code>acc_manifest_replacement_acc_manifest_id_fk</code> key.
     */
    public AccManifest accManifestReplacementAccManifestIdFk() {
        if (_accManifestReplacementAccManifestIdFk == null)
            _accManifestReplacementAccManifestIdFk = new AccManifest(this, Keys.ACC_MANIFEST_REPLACEMENT_ACC_MANIFEST_ID_FK);

        return _accManifestReplacementAccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table,
     * via the <code>acc_manifest_prev_acc_manifest_id_fk</code> key.
     */
    public AccManifest accManifestPrevAccManifestIdFk() {
        if (_accManifestPrevAccManifestIdFk == null)
            _accManifestPrevAccManifestIdFk = new AccManifest(this, Keys.ACC_MANIFEST_PREV_ACC_MANIFEST_ID_FK);

        return _accManifestPrevAccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table,
     * via the <code>acc_manifest_next_acc_manifest_id_fk</code> key.
     */
    public AccManifest accManifestNextAccManifestIdFk() {
        if (_accManifestNextAccManifestIdFk == null)
            _accManifestNextAccManifestIdFk = new AccManifest(this, Keys.ACC_MANIFEST_NEXT_ACC_MANIFEST_ID_FK);

        return _accManifestNextAccManifestIdFk;
    }

    @Override
    public AccManifest as(String alias) {
        return new AccManifest(DSL.name(alias), this);
    }

    @Override
    public AccManifest as(Name alias) {
        return new AccManifest(alias, this);
    }

    @Override
    public AccManifest as(Table<?> alias) {
        return new AccManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifest rename(String name) {
        return new AccManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifest rename(Name name) {
        return new AccManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AccManifest rename(Table<?> name) {
        return new AccManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<String, String, String, String, Byte, String, String, String, String> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
