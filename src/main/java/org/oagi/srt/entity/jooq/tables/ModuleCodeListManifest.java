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
import org.oagi.srt.entity.jooq.tables.records.ModuleCodeListManifestRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleCodeListManifest extends TableImpl<ModuleCodeListManifestRecord> {

    private static final long serialVersionUID = 1592268553;

    /**
     * The reference instance of <code>oagi.module_code_list_manifest</code>
     */
    public static final ModuleCodeListManifest MODULE_CODE_LIST_MANIFEST = new ModuleCodeListManifest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleCodeListManifestRecord> getRecordType() {
        return ModuleCodeListManifestRecord.class;
    }

    /**
     * The column <code>oagi.module_code_list_manifest.module_code_list_manifest_id</code>. Primary key.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> MODULE_CODE_LIST_MANIFEST_ID = createField(DSL.name("module_code_list_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_code_list_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_code_list_manifest.code_list_manifest_id</code>. A foreign key of the code list manifest record.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> CODE_LIST_MANIFEST_ID = createField(DSL.name("code_list_manifest_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the code list manifest record.");

    /**
     * The column <code>oagi.module_code_list_manifest.module_id</code>. A foreign key of the module record.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module record.");

    /**
     * The column <code>oagi.module_code_list_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_code_list_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleCodeListManifestRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_code_list_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleCodeListManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_code_list_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleCodeListManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_code_list_manifest</code> table reference
     */
    public ModuleCodeListManifest() {
        this(DSL.name("module_code_list_manifest"), null);
    }

    /**
     * Create an aliased <code>oagi.module_code_list_manifest</code> table reference
     */
    public ModuleCodeListManifest(String alias) {
        this(DSL.name(alias), MODULE_CODE_LIST_MANIFEST);
    }

    /**
     * Create an aliased <code>oagi.module_code_list_manifest</code> table reference
     */
    public ModuleCodeListManifest(Name alias) {
        this(alias, MODULE_CODE_LIST_MANIFEST);
    }

    private ModuleCodeListManifest(Name alias, Table<ModuleCodeListManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleCodeListManifest(Name alias, Table<ModuleCodeListManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleCodeListManifest(Table<O> child, ForeignKey<O, ModuleCodeListManifestRecord> key) {
        super(child, key, MODULE_CODE_LIST_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleCodeListManifestRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_CODE_LIST_MANIFEST;
    }

    @Override
    public UniqueKey<ModuleCodeListManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_CODE_LIST_MANIFEST_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleCodeListManifestRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleCodeListManifestRecord>>asList(Keys.KEY_MODULE_CODE_LIST_MANIFEST_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleCodeListManifestRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleCodeListManifestRecord, ?>>asList(Keys.MODULE_CODE_LIST_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_CODE_LIST_MANIFEST_CODE_LIST_MANIFEST_ID_FK, Keys.MODULE_CODE_LIST_MANIFEST_MODULE_ID_FK, Keys.MODULE_CODE_LIST_MANIFEST_CREATED_BY_FK, Keys.MODULE_CODE_LIST_MANIFEST_LAST_UPDATED_BY_FK);
    }

    public ModuleSetRelease moduleSetRelease() {
        return new ModuleSetRelease(this, Keys.MODULE_CODE_LIST_MANIFEST_MODULE_SET_RELEASE_ID_FK);
    }

    public CodeListManifest codeListManifest() {
        return new CodeListManifest(this, Keys.MODULE_CODE_LIST_MANIFEST_CODE_LIST_MANIFEST_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.MODULE_CODE_LIST_MANIFEST_MODULE_ID_FK);
    }

    public AppUser moduleCodeListManifestCreatedByFk() {
        return new AppUser(this, Keys.MODULE_CODE_LIST_MANIFEST_CREATED_BY_FK);
    }

    public AppUser moduleCodeListManifestLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_CODE_LIST_MANIFEST_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleCodeListManifest as(String alias) {
        return new ModuleCodeListManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleCodeListManifest as(Name alias) {
        return new ModuleCodeListManifest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleCodeListManifest rename(String name) {
        return new ModuleCodeListManifest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleCodeListManifest rename(Name name) {
        return new ModuleCodeListManifest(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
