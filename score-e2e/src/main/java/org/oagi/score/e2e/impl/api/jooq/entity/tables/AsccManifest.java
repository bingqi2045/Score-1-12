/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function10;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
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
import org.oagi.score.e2e.impl.api.jooq.entity.Keys;
import org.oagi.score.e2e.impl.api.jooq.entity.Oagi;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.records.AsccManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AsccManifest extends TableImpl<AsccManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.ascc_manifest</code>
     */
    public static final AsccManifest ASCC_MANIFEST = new AsccManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AsccManifestRecord> getRecordType() {
        return AsccManifestRecord.class;
    }

    /**
     * The column <code>oagi.ascc_manifest.ascc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> ASCC_MANIFEST_ID = createField(DSL.name("ascc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.ascc_manifest.release_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.ascc_manifest.ascc_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> ASCC_ID = createField(DSL.name("ascc_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.seq_key_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> SEQ_KEY_ID = createField(DSL.name("seq_key_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.ascc_manifest.from_acc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> FROM_ACC_MANIFEST_ID = createField(DSL.name("from_acc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.to_asccp_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> TO_ASCCP_MANIFEST_ID = createField(DSL.name("to_asccp_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.ascc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<AsccManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column <code>oagi.ascc_manifest.replacement_ascc_manifest_id</code>.
     * This refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<AsccManifestRecord, ULong> REPLACEMENT_ASCC_MANIFEST_ID = createField(DSL.name("replacement_ascc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column <code>oagi.ascc_manifest.prev_ascc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> PREV_ASCC_MANIFEST_ID = createField(DSL.name("prev_ascc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.ascc_manifest.next_ascc_manifest_id</code>.
     */
    public final TableField<AsccManifestRecord, ULong> NEXT_ASCC_MANIFEST_ID = createField(DSL.name("next_ascc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    private AsccManifest(Name alias, Table<AsccManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private AsccManifest(Name alias, Table<AsccManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest(String alias) {
        this(DSL.name(alias), ASCC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest(Name alias) {
        this(alias, ASCC_MANIFEST);
    }

    /**
     * Create a <code>oagi.ascc_manifest</code> table reference
     */
    public AsccManifest() {
        this(DSL.name("ascc_manifest"), null);
    }

    public <O extends Record> AsccManifest(Table<O> child, ForeignKey<O, AsccManifestRecord> key) {
        super(child, key, ASCC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<AsccManifestRecord, ULong> getIdentity() {
        return (Identity<AsccManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<AsccManifestRecord> getPrimaryKey() {
        return Keys.KEY_ASCC_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<AsccManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.ASCC_MANIFEST_RELEASE_ID_FK, Keys.ASCC_MANIFEST_ASCC_ID_FK, Keys.ASCC_MANIFEST_SEQ_KEY_ID_FK, Keys.ASCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK, Keys.ASCC_MANIFEST_TO_ASCCP_MANIFEST_ID_FK, Keys.ASCC_REPLACEMENT_ASCC_MANIFEST_ID_FK, Keys.ASCC_MANIFEST_PREV_ASCC_MANIFEST_ID_FK, Keys.ASCC_MANIFEST_NEXT_ASCC_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient Ascc _ascc;
    private transient SeqKey _seqKey;
    private transient AccManifest _accManifest;
    private transient AsccpManifest _asccpManifest;
    private transient AsccManifest _asccReplacementAsccManifestIdFk;
    private transient AsccManifest _asccManifestPrevAsccManifestIdFk;
    private transient AsccManifest _asccManifestNextAsccManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.ASCC_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.ascc</code> table.
     */
    public Ascc ascc() {
        if (_ascc == null)
            _ascc = new Ascc(this, Keys.ASCC_MANIFEST_ASCC_ID_FK);

        return _ascc;
    }

    /**
     * Get the implicit join path to the <code>oagi.seq_key</code> table.
     */
    public SeqKey seqKey() {
        if (_seqKey == null)
            _seqKey = new SeqKey(this, Keys.ASCC_MANIFEST_SEQ_KEY_ID_FK);

        return _seqKey;
    }

    /**
     * Get the implicit join path to the <code>oagi.acc_manifest</code> table.
     */
    public AccManifest accManifest() {
        if (_accManifest == null)
            _accManifest = new AccManifest(this, Keys.ASCC_MANIFEST_FROM_ACC_MANIFEST_ID_FK);

        return _accManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.asccp_manifest</code> table.
     */
    public AsccpManifest asccpManifest() {
        if (_asccpManifest == null)
            _asccpManifest = new AsccpManifest(this, Keys.ASCC_MANIFEST_TO_ASCCP_MANIFEST_ID_FK);

        return _asccpManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.ascc_manifest</code> table,
     * via the <code>ascc_replacement_ascc_manifest_id_fk</code> key.
     */
    public AsccManifest asccReplacementAsccManifestIdFk() {
        if (_asccReplacementAsccManifestIdFk == null)
            _asccReplacementAsccManifestIdFk = new AsccManifest(this, Keys.ASCC_REPLACEMENT_ASCC_MANIFEST_ID_FK);

        return _asccReplacementAsccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.ascc_manifest</code> table,
     * via the <code>ascc_manifest_prev_ascc_manifest_id_fk</code> key.
     */
    public AsccManifest asccManifestPrevAsccManifestIdFk() {
        if (_asccManifestPrevAsccManifestIdFk == null)
            _asccManifestPrevAsccManifestIdFk = new AsccManifest(this, Keys.ASCC_MANIFEST_PREV_ASCC_MANIFEST_ID_FK);

        return _asccManifestPrevAsccManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.ascc_manifest</code> table,
     * via the <code>ascc_manifest_next_ascc_manifest_id_fk</code> key.
     */
    public AsccManifest asccManifestNextAsccManifestIdFk() {
        if (_asccManifestNextAsccManifestIdFk == null)
            _asccManifestNextAsccManifestIdFk = new AsccManifest(this, Keys.ASCC_MANIFEST_NEXT_ASCC_MANIFEST_ID_FK);

        return _asccManifestNextAsccManifestIdFk;
    }

    @Override
    public AsccManifest as(String alias) {
        return new AsccManifest(DSL.name(alias), this);
    }

    @Override
    public AsccManifest as(Name alias) {
        return new AsccManifest(alias, this);
    }

    @Override
    public AsccManifest as(Table<?> alias) {
        return new AsccManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccManifest rename(String name) {
        return new AsccManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccManifest rename(Name name) {
        return new AsccManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public AsccManifest rename(Table<?> name) {
        return new AsccManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<ULong, ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super Byte, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super Byte, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}