/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.DtScManifestRecord;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class DtScManifest extends TableImpl<DtScManifestRecord> {

    private static final long serialVersionUID = 1182926706;

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
    public final TableField<DtScManifestRecord, ULong> DT_SC_MANIFEST_ID = createField(DSL.name("dt_sc_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.release_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.dt_sc_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> DT_SC_ID = createField(DSL.name("dt_sc_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_sc_manifest.owner_dt_manifest_id</code>.
     */
    public final TableField<DtScManifestRecord, ULong> OWNER_DT_MANIFEST_ID = createField(DSL.name("owner_dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.dt_sc_manifest</code> table reference
     */
    public DtScManifest() {
        this(DSL.name("dt_sc_manifest"), null);
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

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtScManifest(Name alias, Table<DtScManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> DtScManifest(Table<O> child, ForeignKey<O, DtScManifestRecord> key) {
        super(child, key, DT_SC_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<DtScManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_DT_SC_MANIFEST;
    }

    @Override
    public UniqueKey<DtScManifestRecord> getPrimaryKey() {
        return Keys.KEY_DT_SC_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<DtScManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<DtScManifestRecord>>asList(Keys.KEY_DT_SC_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<DtScManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DtScManifestRecord, ?>>asList(Keys.DT_SC_MANIFEST_RELEASE_ID_FK, Keys.DT_SC_MANIFEST_DT_SC_ID_FK, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.DT_SC_MANIFEST_RELEASE_ID_FK);
    }

    public DtSc dtSc() {
        return new DtSc(this, Keys.DT_SC_MANIFEST_DT_SC_ID_FK);
    }

    public DtManifest dtManifest() {
        return new DtManifest(this, Keys.DT_SC_MANIFEST_OWNER_DT_MANIFEST_ID_FK);
    }

    @Override
    public DtScManifest as(String alias) {
        return new DtScManifest(DSL.name(alias), this);
    }

    @Override
    public DtScManifest as(Name alias) {
        return new DtScManifest(alias, this);
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

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
