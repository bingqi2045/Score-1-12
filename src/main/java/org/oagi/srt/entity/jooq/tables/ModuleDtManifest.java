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
import org.oagi.srt.entity.jooq.tables.records.ModuleDtManifestRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDtManifest extends TableImpl<ModuleDtManifestRecord> {

    private static final long serialVersionUID = -299156982;

    /**
     * The reference instance of <code>oagi.module_dt_manifest</code>
     */
    public static final ModuleDtManifest MODULE_DT_MANIFEST = new ModuleDtManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleDtManifestRecord> getRecordType() {
        return ModuleDtManifestRecord.class;
    }

    /**
     * The column <code>oagi.module_dt_manifest.module_dt_manifest_id</code>. Primary key.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_DT_MANIFEST_ID = createField(DSL.name("module_dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_dt_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_dt_manifest.dt_manifest_id</code>. A foreign key of the dt manifest record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> DT_MANIFEST_ID = createField(DSL.name("dt_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the dt manifest record.");

    /**
     * The column <code>oagi.module_dt_manifest.module_id</code>. A foreign key of the module record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module record.");

    /**
     * The column <code>oagi.module_dt_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_dt_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_dt_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleDtManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_dt_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleDtManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_dt_manifest</code> table reference
     */
    public ModuleDtManifest() {
        this(DSL.name("module_dt_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.module_dt_manifest</code> table reference
     */
    public ModuleDtManifest(String alias) {
        this(DSL.name(alias), MODULE_DT_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.module_dt_manifest</code> table reference
     */
    public ModuleDtManifest(Name alias) {
        this(alias, MODULE_DT_MANIFEST);
    }

    private ModuleDtManifest(Name alias, Table<ModuleDtManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleDtManifest(Name alias, Table<ModuleDtManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleDtManifest(Table<O> child, ForeignKey<O, ModuleDtManifestRecord> key) {
        super(child, key, MODULE_DT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleDtManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_DT_MANIFEST;
    }

    @Override
    public UniqueKey<ModuleDtManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_DT_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleDtManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleDtManifestRecord>>asList(Keys.KEY_MODULE_DT_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleDtManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleDtManifestRecord, ?>>asList(Keys.MODULE_DT_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_DT_MANIFEST_DT_MANIFEST_ID_FK, Keys.MODULE_DT_MANIFEST_MODULE_ID_FK, Keys.MODULE_DT_MANIFEST_CREATED_BY_FK, Keys.MODULE_DT_MANIFEST_LAST_UPDATED_BY_FK);
    }

    public ModuleSetRelease moduleSetRelease() {
        return new ModuleSetRelease(this, Keys.MODULE_DT_MANIFEST_MODULE_SET_RELEASE_ID_FK);
    }

    public DtManifest dtManifest() {
        return new DtManifest(this, Keys.MODULE_DT_MANIFEST_DT_MANIFEST_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.MODULE_DT_MANIFEST_MODULE_ID_FK);
    }

    public AppUser moduleDtManifestCreatedByFk() {
        return new AppUser(this, Keys.MODULE_DT_MANIFEST_CREATED_BY_FK);
    }

    public AppUser moduleDtManifestLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_DT_MANIFEST_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleDtManifest as(String alias) {
        return new ModuleDtManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleDtManifest as(Name alias) {
        return new ModuleDtManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDtManifest rename(String name) {
        return new ModuleDtManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDtManifest rename(Name name) {
        return new ModuleDtManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
