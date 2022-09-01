/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function8;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row8;
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
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleDtManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDtManifest extends TableImpl<ModuleDtManifestRecord> {

    private static final long serialVersionUID = 1L;

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
     * The column <code>oagi.module_dt_manifest.module_dt_manifest_id</code>.
     * Primary key.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_DT_MANIFEST_ID = createField(DSL.name("module_dt_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_dt_manifest.module_set_release_id</code>. A
     * foreign key of the module set release record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_dt_manifest.dt_manifest_id</code>. A foreign
     * key of the dt manifest record.
     */
    public final TableField<ModuleDtManifestRecord, ULong> DT_MANIFEST_ID = createField(DSL.name("dt_manifest_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the dt manifest record.");

    /**
     * The column <code>oagi.module_dt_manifest.module_id</code>. This indicates
     * a module.
     */
    public final TableField<ModuleDtManifestRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This indicates a module.");

    /**
     * The column <code>oagi.module_dt_manifest.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleDtManifestRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_dt_manifest.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public final TableField<ModuleDtManifestRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_dt_manifest.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public final TableField<ModuleDtManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_dt_manifest.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public final TableField<ModuleDtManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    private ModuleDtManifest(Name alias, Table<ModuleDtManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleDtManifest(Name alias, Table<ModuleDtManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
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

    /**
     * Create a <code>oagi.module_dt_manifest</code> table reference
     */
    public ModuleDtManifest() {
        this(DSL.name("module_dt_manifest"), null);
    }

    public <O extends Record> ModuleDtManifest(Table<O> child, ForeignKey<O, ModuleDtManifestRecord> key) {
        super(child, key, MODULE_DT_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<ModuleDtManifestRecord, ULong> getIdentity() {
        return (Identity<ModuleDtManifestRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ModuleDtManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_DT_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<ModuleDtManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MODULE_DT_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_DT_MANIFEST_DT_MANIFEST_ID_FK, Keys.MODULE_DT_MANIFEST_MODULE_ID_FK, Keys.MODULE_DT_MANIFEST_CREATED_BY_FK, Keys.MODULE_DT_MANIFEST_LAST_UPDATED_BY_FK);
    }

    private transient ModuleSetRelease _moduleSetRelease;
    private transient DtManifest _dtManifest;
    private transient Module _module;
    private transient AppUser _moduleDtManifestCreatedByFk;
    private transient AppUser _moduleDtManifestLastUpdatedByFk;

    /**
     * Get the implicit join path to the <code>oagi.module_set_release</code>
     * table.
     */
    public ModuleSetRelease moduleSetRelease() {
        if (_moduleSetRelease == null)
            _moduleSetRelease = new ModuleSetRelease(this, Keys.MODULE_DT_MANIFEST_MODULE_SET_RELEASE_ID_FK);

        return _moduleSetRelease;
    }

    /**
     * Get the implicit join path to the <code>oagi.dt_manifest</code> table.
     */
    public DtManifest dtManifest() {
        if (_dtManifest == null)
            _dtManifest = new DtManifest(this, Keys.MODULE_DT_MANIFEST_DT_MANIFEST_ID_FK);

        return _dtManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.module</code> table.
     */
    public Module module() {
        if (_module == null)
            _module = new Module(this, Keys.MODULE_DT_MANIFEST_MODULE_ID_FK);

        return _module;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_dt_manifest_created_by_fk</code> key.
     */
    public AppUser moduleDtManifestCreatedByFk() {
        if (_moduleDtManifestCreatedByFk == null)
            _moduleDtManifestCreatedByFk = new AppUser(this, Keys.MODULE_DT_MANIFEST_CREATED_BY_FK);

        return _moduleDtManifestCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_dt_manifest_last_updated_by_fk</code> key.
     */
    public AppUser moduleDtManifestLastUpdatedByFk() {
        if (_moduleDtManifestLastUpdatedByFk == null)
            _moduleDtManifestLastUpdatedByFk = new AppUser(this, Keys.MODULE_DT_MANIFEST_LAST_UPDATED_BY_FK);

        return _moduleDtManifestLastUpdatedByFk;
    }

    @Override
    public ModuleDtManifest as(String alias) {
        return new ModuleDtManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleDtManifest as(Name alias) {
        return new ModuleDtManifest(alias, this);
    }

    @Override
    public ModuleDtManifest as(Table<?> alias) {
        return new ModuleDtManifest(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public ModuleDtManifest rename(Table<?> name) {
        return new ModuleDtManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, ULong, ULong, ULong, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function8<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function8<? super ULong, ? super ULong, ? super ULong, ? super ULong, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
