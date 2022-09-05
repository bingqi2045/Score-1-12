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
import org.oagi.score.repo.api.impl.jooq.entity.Keys;
import org.oagi.score.repo.api.impl.jooq.entity.Oagi;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleBccpManifestRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleBccpManifest extends TableImpl<ModuleBccpManifestRecord> {

    private static final long serialVersionUID = 1L;

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
     * The column
     * <code>oagi.module_bccp_manifest.module_bccp_manifest_id</code>. Primary,
     * internal database key.
     */
    public final TableField<ModuleBccpManifestRecord, String> MODULE_BCCP_MANIFEST_ID = createField(DSL.name("module_bccp_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.module_bccp_manifest.module_set_release_id</code>.
     * A foreign key of the module set release record.
     */
    public final TableField<ModuleBccpManifestRecord, String> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key of the module set release record.");

    /**
     * The column <code>oagi.module_bccp_manifest.bccp_manifest_id</code>. A
     * foreign key of the bccp manifest record.
     */
    public final TableField<ModuleBccpManifestRecord, String> BCCP_MANIFEST_ID = createField(DSL.name("bccp_manifest_id"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key of the bccp manifest record.");

    /**
     * The column <code>oagi.module_bccp_manifest.module_id</code>. This
     * indicates a module.
     */
    public final TableField<ModuleBccpManifestRecord, String> MODULE_ID = createField(DSL.name("module_id"), SQLDataType.CHAR(36).nullable(false), this, "This indicates a module.");

    /**
     * The column <code>oagi.module_bccp_manifest.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created this record.
     */
    public final TableField<ModuleBccpManifestRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this record.");

    /**
     * The column <code>oagi.module_bccp_manifest.last_updated_by</code>.
     * Foreign key to the APP_USER table referring to the last user who updated
     * the record.
     */
    public final TableField<ModuleBccpManifestRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_bccp_manifest.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public final TableField<ModuleBccpManifestRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_bccp_manifest.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public final TableField<ModuleBccpManifestRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    private ModuleBccpManifest(Name alias, Table<ModuleBccpManifestRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleBccpManifest(Name alias, Table<ModuleBccpManifestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
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

    /**
     * Create a <code>oagi.module_bccp_manifest</code> table reference
     */
    public ModuleBccpManifest() {
        this(DSL.name("module_bccp_manifest"), null);
    }

    public <O extends Record> ModuleBccpManifest(Table<O> child, ForeignKey<O, ModuleBccpManifestRecord> key) {
        super(child, key, MODULE_BCCP_MANIFEST);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<ModuleBccpManifestRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_BCCP_MANIFEST_PRIMARY;
    }

    @Override
    public List<ForeignKey<ModuleBccpManifestRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MODULE_BCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK, Keys.MODULE_BCCP_MANIFEST_BCCP_MANIFEST_ID_FK, Keys.MODULE_BCCP_MANIFEST_MODULE_ID_FK, Keys.MODULE_BCCP_MANIFEST_CREATED_BY_FK, Keys.MODULE_BCCP_MANIFEST_LAST_UPDATED_BY_FK);
    }

    private transient ModuleSetRelease _moduleSetRelease;
    private transient BccpManifest _bccpManifest;
    private transient Module _module;
    private transient AppUser _moduleBccpManifestCreatedByFk;
    private transient AppUser _moduleBccpManifestLastUpdatedByFk;

    /**
     * Get the implicit join path to the <code>oagi.module_set_release</code>
     * table.
     */
    public ModuleSetRelease moduleSetRelease() {
        if (_moduleSetRelease == null)
            _moduleSetRelease = new ModuleSetRelease(this, Keys.MODULE_BCCP_MANIFEST_MODULE_SET_RELEASE_ID_FK);

        return _moduleSetRelease;
    }

    /**
     * Get the implicit join path to the <code>oagi.bccp_manifest</code> table.
     */
    public BccpManifest bccpManifest() {
        if (_bccpManifest == null)
            _bccpManifest = new BccpManifest(this, Keys.MODULE_BCCP_MANIFEST_BCCP_MANIFEST_ID_FK);

        return _bccpManifest;
    }

    /**
     * Get the implicit join path to the <code>oagi.module</code> table.
     */
    public Module module() {
        if (_module == null)
            _module = new Module(this, Keys.MODULE_BCCP_MANIFEST_MODULE_ID_FK);

        return _module;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_bccp_manifest_created_by_fk</code> key.
     */
    public AppUser moduleBccpManifestCreatedByFk() {
        if (_moduleBccpManifestCreatedByFk == null)
            _moduleBccpManifestCreatedByFk = new AppUser(this, Keys.MODULE_BCCP_MANIFEST_CREATED_BY_FK);

        return _moduleBccpManifestCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_bccp_manifest_last_updated_by_fk</code> key.
     */
    public AppUser moduleBccpManifestLastUpdatedByFk() {
        if (_moduleBccpManifestLastUpdatedByFk == null)
            _moduleBccpManifestLastUpdatedByFk = new AppUser(this, Keys.MODULE_BCCP_MANIFEST_LAST_UPDATED_BY_FK);

        return _moduleBccpManifestLastUpdatedByFk;
    }

    @Override
    public ModuleBccpManifest as(String alias) {
        return new ModuleBccpManifest(DSL.name(alias), this);
    }

    @Override
    public ModuleBccpManifest as(Name alias) {
        return new ModuleBccpManifest(alias, this);
    }

    @Override
    public ModuleBccpManifest as(Table<?> alias) {
        return new ModuleBccpManifest(alias.getQualifiedName(), this);
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

    /**
     * Rename this table
     */
    @Override
    public ModuleBccpManifest rename(Table<?> name) {
        return new ModuleBccpManifest(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function8<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function8<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
