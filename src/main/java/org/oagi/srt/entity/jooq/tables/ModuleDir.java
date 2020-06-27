/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
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
import org.oagi.srt.entity.jooq.Indexes;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.ModuleDirRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDir extends TableImpl<ModuleDirRecord> {

    private static final long serialVersionUID = 1093532781;

    /**
     * The reference instance of <code>oagi.module_dir</code>
     */
    public static final ModuleDir MODULE_DIR = new ModuleDir();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleDirRecord> getRecordType() {
        return ModuleDirRecord.class;
    }

    /**
     * The column <code>oagi.module_dir.module_dir_id</code>. Primary key.
     */
    public final TableField<ModuleDirRecord, ULong> MODULE_DIR_ID = createField(DSL.name("module_dir_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_dir.parent_module_dir_id</code>. This indicates the parent of this directory.
     */
    public final TableField<ModuleDirRecord, ULong> PARENT_MODULE_DIR_ID = createField(DSL.name("parent_module_dir_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "This indicates the parent of this directory.");

    /**
     * The column <code>oagi.module_dir.name</code>. This is the name of the directory.
     */
    public final TableField<ModuleDirRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "This is the name of the directory.");

    /**
     * The column <code>oagi.module_dir.path</code>. This is a full-path of this module directory for performance.
     */
    public final TableField<ModuleDirRecord, String> PATH = createField(DSL.name("path"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "This is a full-path of this module directory for performance.");

    /**
     * The column <code>oagi.module_dir.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE_DIR.
     */
    public final TableField<ModuleDirRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this MODULE_DIR.");

    /**
     * The column <code>oagi.module_dir.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleDirRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_dir.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleDirRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_dir.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleDirRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_dir</code> table reference
     */
    public ModuleDir() {
        this(DSL.name("module_dir"), null);
    }

    /**
     * Create an aliased <code>oagi.module_dir</code> table reference
     */
    public ModuleDir(String alias) {
        this(DSL.name(alias), MODULE_DIR);
    }

    /**
     * Create an aliased <code>oagi.module_dir</code> table reference
     */
    public ModuleDir(Name alias) {
        this(alias, MODULE_DIR);
    }

    private ModuleDir(Name alias, Table<ModuleDirRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleDir(Name alias, Table<ModuleDirRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleDir(Table<O> child, ForeignKey<O, ModuleDirRecord> key) {
        super(child, key, MODULE_DIR);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MODULE_DIR_MODULE_DIR_PATH_K);
    }

    @Override
    public Identity<ModuleDirRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_DIR;
    }

    @Override
    public UniqueKey<ModuleDirRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_DIR_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleDirRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleDirRecord>>asList(Keys.KEY_MODULE_DIR_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleDirRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleDirRecord, ?>>asList(Keys.MODULE_DIR_PARENT_MODULE_DIR_ID_FK, Keys.MODULE_DIR_CREATED_BY_FK, Keys.MODULE_DIR_LAST_UPDATED_BY_FK);
    }

    public ModuleDir moduleDir() {
        return new ModuleDir(this, Keys.MODULE_DIR_PARENT_MODULE_DIR_ID_FK);
    }

    public AppUser moduleDirCreatedByFk() {
        return new AppUser(this, Keys.MODULE_DIR_CREATED_BY_FK);
    }

    public AppUser moduleDirLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_DIR_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleDir as(String alias) {
        return new ModuleDir(DSL.name(alias), this);
    }

    @Override
    public ModuleDir as(Name alias) {
        return new ModuleDir(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDir rename(String name) {
        return new ModuleDir(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDir rename(Name name) {
        return new ModuleDir(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, String, String, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
