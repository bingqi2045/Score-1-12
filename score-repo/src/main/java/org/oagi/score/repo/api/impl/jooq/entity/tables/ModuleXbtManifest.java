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
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleXbtManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleXbtManifest extends TableImpl<ModuleXbtManifestRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.module_xbt_manifest</code>
     */
    public static final ModuleXbtManifest MODULE_XBT_MANIFEST = new ModuleXbtManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleXbtManifestRecord> getRecordType() {
        return ModuleXbtManifestRecord.class;
    }

    /**
     * The column <code>oagi.module_xbt_manifest.module_xbt_manifest_id</code>. Primary key.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> MODULE_XBT_MANIFEST_ID = createField(DSL.name("module_xbt_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_xbt_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_xbt_manifest.xbt_manifest_id</code>. A foreign key of the xbt manifest record.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> XBT_MANIFEST_ID = createField(DSL.name("xbt_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the xbt manifest record.");

    /**
     * The column <code>oagi.module_xbt_manifest.module_set_assignment_id</code>.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> MODULE_SET_ASSIGNMENT_ID = createField(DSL.name("module_set_assignment_id"), SQLDataType.BIGINTUNSIGNED, this, "");

    /**
     * The column <code>oagi.module_xbt_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_xbt_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleXbtManifestRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_xbt_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleXbtManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_xbt_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleXbtManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    private ModuleXbtManifest(Name alias, Table<ModuleXbtManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleXbtManifest(Name alias, Table<ModuleXbtManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.module_xbt_manifest</code> table reference
     */
    public ModuleXbtManifest(String alias) {
        this(DSL.name(alias), MODULE_XBT_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.module_xbt_manifest</code> table reference
     */
    public ModuleXbtManifest(Name alias) {
        this(alias, MODULE_XBT_MANIFEST);
    }

    /**
     * Create a <code>oagi.module_xbt_manifest</code> table reference
     */
    public ModuleXbtManifest() {
        this(DSL.name("module_xbt_manifest"), null);
    }

    public <O extends Record> ModuleXbtManifest(Table<O> child, ForeignKey<O, ModuleXbtManifestRecord> key) {
        super(child, key, MODULE_XBT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleXbtManifestRecord, ULong> getIdentity() {
        return (Identity<ModuleXbtManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ModuleXbtManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_XBT_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleXbtManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleXbtManifestRecord>>asList(Keys.KEY_MODULE_XBT_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleXbtManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleXbtManifestRecord, ?>>asList(Keys.MODULE_XBT_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_XBT_MANIFEST_BCCP_MANIFEST_ID_FK, Keys.MODULE_XBT_MANIFEST_MODULE_SET_ASSIGNMENT_ID_FK, Keys.MODULE_XBT_MANIFEST_CREATED_BY_FK, Keys.MODULE_XBT_MANIFEST_LAST_UPDATED_BY_FK);
    }

    public ModuleSetRelease moduleSetRelease() {
        return new ModuleSetRelease(this, Keys.MODULE_XBT_MANIFEST_MODULE_SET_RELEASE_ID_FK);
    }

    public XbtManifest xbtManifest() {
        return new XbtManifest(this, Keys.MODULE_XBT_MANIFEST_BCCP_MANIFEST_ID_FK);
    }

    public ModuleSetAssignment moduleSetAssignment() {
        return new ModuleSetAssignment(this, Keys.MODULE_XBT_MANIFEST_MODULE_SET_ASSIGNMENT_ID_FK);
    }

    public AppUser moduleXbtManifestCreatedByFk() {
        return new AppUser(this, Keys.MODULE_XBT_MANIFEST_CREATED_BY_FK);
    }

    public AppUser moduleXbtManifestLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_XBT_MANIFEST_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleXbtManifest as(String alias) {
        return new ModuleXbtManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleXbtManifest as(Name alias) {
        return new ModuleXbtManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleXbtManifest rename(String name) {
        return new ModuleXbtManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleXbtManifest rename(Name name) {
        return new ModuleXbtManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
