/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.entity.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.Keys;
import org.oagi.score.entity.jooq.Oagi;
import org.oagi.score.entity.jooq.tables.records.ModuleBccpManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleBccpManifest extends TableImpl<ModuleBccpManifestRecord> {

    private static final long serialVersionUID = 467420630;

    /**
     * The reference instance of <code>oagi.module_bccp_manifest</code>
     */
    public static final ModuleBccpManifest MODULE_BCCP_MANIFEST = new ModuleBccpManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleBccpManifestRecord> getRecordType() {
        return ModuleBccpManifestRecord.class;
    }

    /**
     * The column <code>oagi.module_bccp_manifest.module_bccp_manifest_id</code>. Primary key.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> MODULE_BCCP_MANIFEST_ID = createField(DSL.name("module_bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_bccp_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_bccp_manifest.bccp_manifest_id</code>. A foreign key of the bccp manifest record.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> BCCP_MANIFEST_ID = createField(DSL.name("bccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the bccp manifest record.");

    /**
     * The column <code>oagi.module_bccp_manifest.module_id</code>. A foreign key of the module record.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module record.");

    /**
     * The column <code>oagi.module_bccp_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_bccp_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleBccpManifestRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_bccp_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleBccpManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_bccp_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleBccpManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_bccp_manifest</code> table reference
     */
    public ModuleBccpManifest() {
        this(DSL.name("module_bccp_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.module_bccp_manifest</code> table reference
     */
    public ModuleBccpManifest(String alias) {
        this(DSL.name(alias), MODULE_BCCP_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.module_bccp_manifest</code> table reference
     */
    public ModuleBccpManifest(Name alias) {
        this(alias, MODULE_BCCP_MANIFEST);
    }

    private ModuleBccpManifest(Name alias, Table<ModuleBccpManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleBccpManifest(Name alias, Table<ModuleBccpManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleBccpManifest(Table<O> child, ForeignKey<O, ModuleBccpManifestRecord> key) {
        super(child, key, MODULE_BCCP_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleBccpManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_BCCP_MANIFEST;
    }

    @Override
    public UniqueKey<ModuleBccpManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_BCCP_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleBccpManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleBccpManifestRecord>>asList(Keys.KEY_MODULE_BCCP_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleBccpManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleBccpManifestRecord, ?>>asList(Keys.MODULE_BCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_BCCP_MANIFEST_BCCP_MANIFEST_ID_FK, Keys.MODULE_BCCP_MANIFEST_MODULE_ID_FK, Keys.MODULE_BCCP_MANIFEST_CREATED_BY_FK, Keys.MODULE_BCCP_MANIFEST_LAST_UPDATED_BY_FK);
    }

    public ModuleSetRelease moduleSetRelease() {
        return new ModuleSetRelease(this, Keys.MODULE_BCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK);
    }

    public BccpManifest bccpManifest() {
        return new BccpManifest(this, Keys.MODULE_BCCP_MANIFEST_BCCP_MANIFEST_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.MODULE_BCCP_MANIFEST_MODULE_ID_FK);
    }

    public AppUser moduleBccpManifestCreatedByFk() {
        return new AppUser(this, Keys.MODULE_BCCP_MANIFEST_CREATED_BY_FK);
    }

    public AppUser moduleBccpManifestLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_BCCP_MANIFEST_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleBccpManifest as(String alias) {
        return new ModuleBccpManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleBccpManifest as(Name alias) {
        return new ModuleBccpManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleBccpManifest rename(String name) {
        return new ModuleBccpManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleBccpManifest rename(Name name) {
        return new ModuleBccpManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
