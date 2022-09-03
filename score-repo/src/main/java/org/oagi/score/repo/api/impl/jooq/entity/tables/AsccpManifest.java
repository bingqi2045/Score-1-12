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
import org.jooq.Identity;
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
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccpManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccpManifest extends TableImpl<AsccpManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.asccp_manifest</code>
     */
    public static final AsccpManifest ASCCP_MANIFEST = new AsccpManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccpManifestRecord> getRecordType() {
        return AsccpManifestRecord.class;
    }

    /**
     * The column <code>oagi.asccp_manifest.asccp_manifest_id</code>.
     */
    public final TableField<AsccpManifestRecord, ULong> ASCCP_MANIFEST_ID = createField(DSL.name("asccp_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.asccp_manifest.release_id</code>. Foreign key to
     * the RELEASE table.
     */
    public final TableField<AsccpManifestRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the RELEASE table.");

    /**
     * The column <code>oagi.asccp_manifest.asccp_id</code>.
     */
    public final TableField<AsccpManifestRecord, String> ASCCP_ID = createField(DSL.name("asccp_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>oagi.asccp_manifest.role_of_acc_manifest_id</code>.
     */
    public final TableField<AsccpManifestRecord, ULong> ROLE_OF_ACC_MANIFEST_ID = createField(DSL.name("role_of_acc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.asccp_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<AsccpManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.asccp_manifest.log_id</code>. A foreign key pointed
     * to a log for the current record.
     */
    public final TableField<AsccpManifestRecord, String> LOG_ID = createField(DSL.name("log_id"), SQLDataType.CHAR(36), this, "A foreign key pointed to a log for the current record.");

    /**
     * The column
     * <code>oagi.asccp_manifest.replacement_asccp_manifest_id</code>. This
     * refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<AsccpManifestRecord, ULong> REPLACEMENT_ASCCP_MANIFEST_ID = createField(DSL.name("replacement_asccp_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column <code>oagi.asccp_manifest.prev_asccp_manifest_id</code>.
     */
    public final TableField<AsccpManifestRecord, ULong> PREV_ASCCP_MANIFEST_ID = createField(DSL.name("prev_asccp_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.asccp_manifest.next_asccp_manifest_id</code>.
     */
    public final TableField<AsccpManifestRecord, ULong> NEXT_ASCCP_MANIFEST_ID = createField(DSL.name("next_asccp_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    private AsccpManifest(Name alias, Table<AsccpManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AsccpManifest(Name alias, Table<AsccpManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.asccp_manifest</code> table reference
     */
    public AsccpManifest(String alias) {
        this(DSL.name(alias), ASCCP_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.asccp_manifest</code> table reference
     */
    public AsccpManifest(Name alias) {
        this(alias, ASCCP_MANIFEST);
    }

    /**
     * Create a <code>oagi.asccp_manifest</code> table reference
     */
    public AsccpManifest() {
        this(DSL.name("asccp_manifest"), null);
    }

    public <O extends Record> AsccpManifest(Table<O> child, ForeignKey<O, AsccpManifestRecord> key) {
        super(child, key, ASCCP_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<AsccpManifestRecord, ULong> getIdentity() {
        return (Identity<AsccpManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AsccpManifestRecord> getPrimaryKey() {
        return Keys.KEY_ASCCP_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<AsccpManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ASCCP_MANIFEST_RELEASE_ID_FK, Keys.ASCCP_MANIFEST_ASCCP_ID_FK, Keys.ASCCP_MANIFEST_ROLE_OF_ACC_MANIFEST_ID_FK, Keys.ASCCP_MANIFEST_LOG_ID_FK, Keys.ASCCP_REPLACEMENT_ASCCP_MANIFEST_ID_FK, Keys.ASCCP_MANIFEST_PREV_ASCCP_MANIFEST_ID_FK, Keys.ASCCP_MANIFEST_NEXT_ASCCP_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient Asccp _asccp;
    private transient AccManifest _accManifest;
    private transient Log _log;
    private transient AsccpManifest _asccpReplacementAsccpManifestIdFk;
    private transient AsccpManifest _asccpManifestPrevAsccpManifestIdFk;
    private transient AsccpManifest _asccpManifestNextAsccpManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.ASCCP_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.asccp</code> table.
     */
    public Asccp asccp() {
        if (_asccp == null)
            _asccp = new Asccp(this, Keys.ASCCP_MANIFEST_ASCCP_ID_FK);

        return _asccp;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table.
     */
    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.ASCCP_MANIFEST_ROLE_OF_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.log</code> table.
     */
    public Log log() {
        if (_log == null)
            _log = new Log(this, Keys.ASCCP_MANIFEST_LOG_ID_FK);

        return _log;
    }

    /**
     * Get the implicit join path to the <code>oagi.asccp_manifest</code> table,
     * via the <code>asccp_replacement_asccp_manifest_id_fk</code> key.
     */
    public AsccpManifest asccpReplacementAsccpManifestIdFk() {
        if (_asccpReplacementAsccpManifestIdFk == null)
            _asccpReplacementAsccpManifestIdFk = new AsccpManifest(this, Keys.ASCCP_REPLACEMENT_ASCCP_MANIFEST_ID_FK);

        return _asccpReplacementAsccpManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.asccp_manifest</code> table,
     * via the <code>asccp_manifest_prev_asccp_manifest_id_fk</code> key.
     */
    public AsccpManifest asccpManifestPrevAsccpManifestIdFk() {
        if (_asccpManifestPrevAsccpManifestIdFk == null)
            _asccpManifestPrevAsccpManifestIdFk = new AsccpManifest(this, Keys.ASCCP_MANIFEST_PREV_ASCCP_MANIFEST_ID_FK);

        return _asccpManifestPrevAsccpManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.asccp_manifest</code> table,
     * via the <code>asccp_manifest_next_asccp_manifest_id_fk</code> key.
     */
    public AsccpManifest asccpManifestNextAsccpManifestIdFk() {
        if (_asccpManifestNextAsccpManifestIdFk == null)
            _asccpManifestNextAsccpManifestIdFk = new AsccpManifest(this, Keys.ASCCP_MANIFEST_NEXT_ASCCP_MANIFEST_ID_FK);

        return _asccpManifestNextAsccpManifestIdFk;
    }

    @Override
    public AsccpManifest as(String alias) {
        return new AsccpManifest(DSL.name(alias), this);
    }

    @Override
    public AsccpManifest as(Name alias) {
        return new AsccpManifest(alias, this);
    }

    @Override
    public AsccpManifest as(Table<?> alias) {
        return new AsccpManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccpManifest rename(String name) {
        return new AsccpManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccpManifest rename(Name name) {
        return new AsccpManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccpManifest rename(Table<?> name) {
        return new AsccpManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, String, String, ULong, Byte, String, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super ULong, ? super String, ? super String, ? super ULong, ? super Byte, ? super String, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super ULong, ? super String, ? super String, ? super ULong, ? super Byte, ? super String, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
