/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


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
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleAsccpManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleAsccpManifest extends TableImpl<ModuleAsccpManifestRecord> {

    private static final long serialVersionUID = 1336832802;

    /**
     * The reference instance of <code>oagi.module_asccp_manifest</code>
     */
    public static final ModuleAsccpManifest MODULE_ASCCP_MANIFEST = new ModuleAsccpManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleAsccpManifestRecord> getRecordType() {
        return ModuleAsccpManifestRecord.class;
    }

    /**
     * The column <code>oagi.module_asccp_manifest.module_asccp_manifest_id</code>. Primary key.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> MODULE_ASCCP_MANIFEST_ID = createField(DSL.name("module_asccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_asccp_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_asccp_manifest.asccp_manifest_id</code>. A foreign key of the asccp manifest record.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> ASCCP_MANIFEST_ID = createField(DSL.name("asccp_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the asccp manifest record.");

    /**
     * The column <code>oagi.module_asccp_manifest.module_id</code>. A foreign key of the module record.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module record.");

    /**
     * The column <code>oagi.module_asccp_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_asccp_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleAsccpManifestRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_asccp_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleAsccpManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_asccp_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleAsccpManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_asccp_manifest</code> table reference
     */
    public ModuleAsccpManifest() {
        this(DSL.name("module_asccp_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.module_asccp_manifest</code> table reference
     */
    public ModuleAsccpManifest(String alias) {
        this(DSL.name(alias), MODULE_ASCCP_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.module_asccp_manifest</code> table reference
     */
    public ModuleAsccpManifest(Name alias) {
        this(alias, MODULE_ASCCP_MANIFEST);
    }

    private ModuleAsccpManifest(Name alias, Table<ModuleAsccpManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleAsccpManifest(Name alias, Table<ModuleAsccpManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleAsccpManifest(Table<O> child, ForeignKey<O, ModuleAsccpManifestRecord> key) {
        super(child, key, MODULE_ASCCP_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleAsccpManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_ASCCP_MANIFEST;
    }

    @Override
    public UniqueKey<ModuleAsccpManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_ASCCP_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleAsccpManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleAsccpManifestRecord>>asList(Keys.KEY_MODULE_ASCCP_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleAsccpManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleAsccpManifestRecord, ?>>asList(Keys.MODULE_ASCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_ASCCP_MANIFEST_ASCCP_MANIFEST_ID_FK, Keys.MODULE_ASCCP_MANIFEST_MODULE_ID_FK, Keys.MODULE_ASCCP_MANIFEST_CREATED_BY_FK, Keys.MODULE_ASCCP_MANIFEST_LAST_UPDATED_BY_FK);
    }

    public ModuleSetRelease moduleSetRelease() {
        return new ModuleSetRelease(this, Keys.MODULE_ASCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK);
    }

    public AsccpManifest asccpManifest() {
        return new AsccpManifest(this, Keys.MODULE_ASCCP_MANIFEST_ASCCP_MANIFEST_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.MODULE_ASCCP_MANIFEST_MODULE_ID_FK);
    }

    public AppUser moduleAsccpManifestCreatedByFk() {
        return new AppUser(this, Keys.MODULE_ASCCP_MANIFEST_CREATED_BY_FK);
    }

    public AppUser moduleAsccpManifestLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_ASCCP_MANIFEST_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleAsccpManifest as(String alias) {
        return new ModuleAsccpManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleAsccpManifest as(Name alias) {
        return new ModuleAsccpManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleAsccpManifest rename(String name) {
        return new ModuleAsccpManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleAsccpManifest rename(Name name) {
        return new ModuleAsccpManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}