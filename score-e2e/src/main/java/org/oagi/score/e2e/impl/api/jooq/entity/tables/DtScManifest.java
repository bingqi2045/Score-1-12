/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.e2e.impl.api.jooq.entity.tables;


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
import org.oagi.score.e2e.impl.api.jooq.entity.Keys;
import org.oagi.score.e2e.impl.api.jooq.entity.Oagi;
import org.oagi.score.e2e.impl.api.jooq.entity.tables.records.DtScManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtScManifest extends TableImpl<DtScManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.dt_sc_manifest</code>
     */
    public static final DtScManifest DT_SC_MANIFEST = new DtScManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtScManifestRecord> getRecordType() {
        return DtScManifestRecord.class;
    }

    /**
     * The column <code>oagi.dt_sc_manifest.dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> DT_SC_MANIFEST_ID = createField(DSL.name("dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.release_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.dt_sc_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> DT_SC_ID = createField(DSL.name("dt_sc_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.owner_dt_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> OWNER_DT_MANIFEST_ID = createField(DSL.name("owner_dt_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.based_dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> BASED_DT_SC_MANIFEST_ID = createField(DSL.name("based_dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.conflict</code>. This indicates that
     * there is a conflict between self and relationship.
     */
    public final TableField<DtScManifestRecord, Byte> CONFLICT = createField(DSL.name("conflict"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "This indicates that there is a conflict between self and relationship.");

    /**
     * The column
     * <code>oagi.dt_sc_manifest.replacement_dt_sc_manifest_id</code>. This
     * refers to a replacement manifest if the record is deprecated.
     */
    public final TableField<DtScManifestRecord, ULong> REPLACEMENT_DT_SC_MANIFEST_ID = createField(DSL.name("replacement_dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "This refers to a replacement manifest if the record is deprecated.");

    /**
     * The column <code>oagi.dt_sc_manifest.prev_dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> PREV_DT_SC_MANIFEST_ID = createField(DSL.name("prev_dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.next_dt_sc_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> NEXT_DT_SC_MANIFEST_ID = createField(DSL.name("next_dt_sc_manifest_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest(String alias) {
        this(DSL.name(alias), DT_SC_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest(Name alias) {
        this(alias, DT_SC_MANIFEST);
    }

    /**
     * Create a <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest() {
        this(DSL.name("dt_sc_manifest"), null);
    }

    public <O extends Record> DtScManifest(Table<O> child, ForeignKey<O, DtScManifestRecord> key) {
        super(child, key, DT_SC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<DtScManifestRecord, ULong> getIdentity() {
        return (Identity<DtScManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<DtScManifestRecord> getPrimaryKey() {
        return Keys.KEY_DT_SC_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<DtScManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.DT_SC_MANIFEST_RELEASE_ID_FK, Keys.DT_SC_MANIFEST_DT_SC_ID_FK, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK, Keys.BASED_DT_SC_MANIFEST_ID_FK, Keys.DT_SC_REPLACEMENT_DT_SC_MANIFEST_ID_FK, Keys.DT_SC_PREV_DT_SC_MANIFEST_ID_FK, Keys.DT_SC_NEXT_DT_SC_MANIFEST_ID_FK);
    }

    private transient Release _release;
    private transient DtSc _dtSc;
    private transient DtManifest _dtManifest;
    private transient DtScManifest _basedDtScManifestIdFk;
    private transient DtScManifest _dtScReplacementDtScManifestIdFk;
    private transient DtScManifest _dtScPrevDtScManifestIdFk;
    private transient DtScManifest _dtScNextDtScManifestIdFk;

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.DT_SC_MANIFEST_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc</code> table.
     */
    public DtSc dtSc() {
        if (_dtSc == null)
            _dtSc = new DtSc(this, Keys.DT_SC_MANIFEST_DT_SC_ID_FK);

        return _dtSc;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_manifest</code> table.
     */
    public DtManifest dtManifest() {
        if (_dtManifest == null)
            _dtManifest = new DtManifest(this, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK);

        return _dtManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc_manifest</code> table,
     * via the <code>based_dt_sc_manifest_id_fk</code> key.
     */
    public DtScManifest basedDtScManifestIdFk() {
        if (_basedDtScManifestIdFk == null)
            _basedDtScManifestIdFk = new DtScManifest(this, Keys.BASED_DT_SC_MANIFEST_ID_FK);

        return _basedDtScManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc_manifest</code> table,
     * via the <code>dt_sc_replacement_dt_sc_manifest_id_fk</code> key.
     */
    public DtScManifest dtScReplacementDtScManifestIdFk() {
        if (_dtScReplacementDtScManifestIdFk == null)
            _dtScReplacementDtScManifestIdFk = new DtScManifest(this, Keys.DT_SC_REPLACEMENT_DT_SC_MANIFEST_ID_FK);

        return _dtScReplacementDtScManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc_manifest</code> table,
     * via the <code>dt_sc_prev_dt_sc_manifest_id_fk</code> key.
     */
    public DtScManifest dtScPrevDtScManifestIdFk() {
        if (_dtScPrevDtScManifestIdFk == null)
            _dtScPrevDtScManifestIdFk = new DtScManifest(this, Keys.DT_SC_PREV_DT_SC_MANIFEST_ID_FK);

        return _dtScPrevDtScManifestIdFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_sc_manifest</code> table,
     * via the <code>dt_sc_next_dt_sc_manifest_id_fk</code> key.
     */
    public DtScManifest dtScNextDtScManifestIdFk() {
        if (_dtScNextDtScManifestIdFk == null)
            _dtScNextDtScManifestIdFk = new DtScManifest(this, Keys.DT_SC_NEXT_DT_SC_MANIFEST_ID_FK);

        return _dtScNextDtScManifestIdFk;
    }

    @Override
    public DtScManifest as(String alias) {
        return new DtScManifest(DSL.name(alias), this);
    }

    @Override
    public DtScManifest as(Name alias) {
        return new DtScManifest(alias, this);
    }

    @Override
    public DtScManifest as(Table<?> alias) {
        return new DtScManifest(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtScManifest rename(String name) {
        return new DtScManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtScManifest rename(Name name) {
        return new DtScManifest(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtScManifest rename(Table<?> name) {
        return new DtScManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<ULong, ULong, ULong, ULong, ULong, Byte, ULong, ULong, ULong> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super Byte, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super Byte, ? super ULong, ? super ULong, ? super ULong, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}