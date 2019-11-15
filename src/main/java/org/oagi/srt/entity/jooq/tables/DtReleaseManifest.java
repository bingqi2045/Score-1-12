/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.DtReleaseManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DtReleaseManifest extends TableImpl<DtReleaseManifestRecord> {

    private static final long serialVersionUID = 165843948;

    /**
     * The reference instance of <code>oagi.dt_release_manifest</code>
     */
    public static final DtReleaseManifest DT_RELEASE_MANIFEST = new DtReleaseManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DtReleaseManifestRecord> getRecordType() {
        return DtReleaseManifestRecord.class;
    }

    /**
     * The column <code>oagi.dt_release_manifest.dt_release_manifest_id</code>.
     */
    public final TableField<DtReleaseManifestRecord, ULong> DT_RELEASE_MANIFEST_ID = createField(DSL.name("dt_release_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>oagi.dt_release_manifest.release_id</code>.
     */
    public final TableField<DtReleaseManifestRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>oagi.dt_release_manifest.module_id</code>.
     */
    public final TableField<DtReleaseManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.dt_release_manifest.dt_id</code>.
     */
    public final TableField<DtReleaseManifestRecord, ULong> DT_ID = createField(DSL.name("dt_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>oagi.dt_release_manifest</code> table reference
     */
    public DtReleaseManifest() {
        this(DSL.name("dt_release_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.dt_release_manifest</code> table reference
     */
    public DtReleaseManifest(String alias) {
        this(DSL.name(alias), DT_RELEASE_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.dt_release_manifest</code> table reference
     */
    public DtReleaseManifest(Name alias) {
        this(alias, DT_RELEASE_MANIFEST);
    }

    private DtReleaseManifest(Name alias, Table<DtReleaseManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private DtReleaseManifest(Name alias, Table<DtReleaseManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> DtReleaseManifest(Table<O> child, ForeignKey<O, DtReleaseManifestRecord> key) {
        super(child, key, DT_RELEASE_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.DT_RELEASE_MANIFEST_DT_RELEASE_MANIFEST_DT_ID_FK, Indexes.DT_RELEASE_MANIFEST_DT_RELEASE_MANIFEST_MODULE_ID_FK, Indexes.DT_RELEASE_MANIFEST_DT_RELEASE_MANIFEST_RELEASE_ID_FK, Indexes.DT_RELEASE_MANIFEST_PRIMARY);
    }

    @Override
    public Identity<DtReleaseManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_DT_RELEASE_MANIFEST;
    }

    @Override
    public UniqueKey<DtReleaseManifestRecord> getPrimaryKey() {
        return Keys.KEY_DT_RELEASE_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<DtReleaseManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<DtReleaseManifestRecord>>asList(Keys.KEY_DT_RELEASE_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<DtReleaseManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DtReleaseManifestRecord, ?>>asList(Keys.DT_RELEASE_MANIFEST_RELEASE_ID_FK, Keys.DT_RELEASE_MANIFEST_MODULE_ID_FK, Keys.DT_RELEASE_MANIFEST_DT_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.DT_RELEASE_MANIFEST_RELEASE_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.DT_RELEASE_MANIFEST_MODULE_ID_FK);
    }

    public Dt dt() {
        return new Dt(this, Keys.DT_RELEASE_MANIFEST_DT_ID_FK);
    }

    @Override
    public DtReleaseManifest as(String alias) {
        return new DtReleaseManifest(DSL.name(alias), this);
    }

    @Override
    public DtReleaseManifest as(Name alias) {
        return new DtReleaseManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DtReleaseManifest rename(String name) {
        return new DtReleaseManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DtReleaseManifest rename(Name name) {
        return new DtReleaseManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, ULong, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
