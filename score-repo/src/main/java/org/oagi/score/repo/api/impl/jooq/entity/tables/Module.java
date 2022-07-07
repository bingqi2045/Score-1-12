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
import org.jooq.Function13;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row13;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleRecord;


/**
 * The module table stores information about a physical file, into which CC
 * components will be generated during the expression generation.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Module extends TableImpl<ModuleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.module</code>
     */
    public static final Module MODULE = new Module();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleRecord> getRecordType() {
        return ModuleRecord.class;
    }

    /**
     * The column <code>oagi.module.module_id</code>. Primary, internal database
     * key.
     */
    public final TableField<ModuleRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.module.module_set_id</code>. This indicates a
     * module set.
     */
    public final TableField<ModuleRecord, ULong> MODULE_SET_ID = createField(DSL.name("module_set_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "This indicates a module set.");

    /**
     * The column <code>oagi.module.parent_module_id</code>. This indicates a
     * parent module id. root module will be NULL.
     */
    public final TableField<ModuleRecord, ULong> PARENT_MODULE_ID = createField(DSL.name("parent_module_id"), SQLDataType.BIGINTUNSIGNED, this, "This indicates a parent module id. root module will be NULL.");

    /**
     * The column <code>oagi.module.type</code>. This is a type column for
     * indicates module is FILE or DIRECTORY.
     */
    public final TableField<ModuleRecord, String> TYPE = createField(DSL.name("type"), SQLDataType.VARCHAR(45).nullable(false), this, "This is a type column for indicates module is FILE or DIRECTORY.");

    /**
     * The column <code>oagi.module.path</code>. Absolute path to the module.
     */
    public final TableField<ModuleRecord, String> PATH = createField(DSL.name("path"), SQLDataType.CLOB.nullable(false), this, "Absolute path to the module.");

    /**
     * The column <code>oagi.module.name</code>. The is the filename of the
     * module. The reason to not including the extension is that the extension
     * maybe dependent on the expression. For XML schema, '.xsd' maybe added; or
     * for JSON, '.json' maybe added as the file extension.
     */
    public final TableField<ModuleRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100).nullable(false), this, "The is the filename of the module. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, '.xsd' maybe added; or for JSON, '.json' maybe added as the file extension.");

    /**
     * The column <code>oagi.module.namespace_id</code>. Note that a release
     * record has a namespace associated. The NAMESPACE_ID, if specified here,
     * overrides the release's namespace. However, the NAMESPACE_ID associated
     * with the component takes the highest precedence.
     */
    public final TableField<ModuleRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), SQLDataType.BIGINTUNSIGNED, this, "Note that a release record has a namespace associated. The NAMESPACE_ID, if specified here, overrides the release's namespace. However, the NAMESPACE_ID associated with the component takes the highest precedence.");

    /**
     * The column <code>oagi.module.version_num</code>. This is the version
     * number to be assigned to the schema module.
     */
    public final TableField<ModuleRecord, String> VERSION_NUM = createField(DSL.name("version_num"), SQLDataType.VARCHAR(45), this, "This is the version number to be assigned to the schema module.");

    /**
     * The column <code>oagi.module.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this MODULE.
     */
    public final TableField<ModuleRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this MODULE.");

    /**
     * The column <code>oagi.module.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<ModuleRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.module.owner_user_id</code>. Foreign key to the
     * APP_USER table identifying the user who can update or delete the record.
     */
    public final TableField<ModuleRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying the user who can update or delete the record.");

    /**
     * The column <code>oagi.module.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public final TableField<ModuleRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     */
    public final TableField<ModuleRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    private Module(Name alias, Table<ModuleRecord> aliased) {
        this(alias, aliased, null);
    }

    private Module(Name alias, Table<ModuleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The module table stores information about a physical file, into which CC components will be generated during the expression generation."), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.module</code> table reference
     */
    public Module(String alias) {
        this(DSL.name(alias), MODULE);
    }

    /**
     * Create an aliased <code>oagi.module</code> table reference
     */
    public Module(Name alias) {
        this(alias, MODULE);
    }

    /**
     * Create a <code>oagi.module</code> table reference
     */
    public Module() {
        this(DSL.name("module"), null);
    }

    public <O extends Record> Module(Table<O> child, ForeignKey<O, ModuleRecord> key) {
        super(child, key, MODULE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public Identity<ModuleRecord, ULong> getIdentity() {
        return (Identity<ModuleRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ModuleRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_PRIMARY;
    }

    @Override
    public List<ForeignKey<ModuleRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MODULE_MODULE_SET_ID_FK, Keys.MODULE_PARENT_MODULE_ID_FK, Keys.MODULE_NAMESPACE_ID_FK, Keys.MODULE_CREATED_BY_FK, Keys.MODULE_LAST_UPDATED_BY_FK, Keys.MODULE_OWNER_USER_ID_FK);
    }

    private transient ModuleSet _moduleSet;
    private transient Module _module;
    private transient Namespace _namespace;
    private transient AppUser _moduleCreatedByFk;
    private transient AppUser _moduleLastUpdatedByFk;
    private transient AppUser _moduleOwnerUserIdFk;

    /**
     * Get the implicit join path to the <code>oagi.module_set</code> table.
     */
    public ModuleSet moduleSet() {
        if (_moduleSet == null)
            _moduleSet = new ModuleSet(this, Keys.MODULE_MODULE_SET_ID_FK);

        return _moduleSet;
    }

    /**
     * Get the implicit join path to the <code>oagi.module</code> table.
     */
    public Module module() {
        if (_module == null)
            _module = new Module(this, Keys.MODULE_PARENT_MODULE_ID_FK);

        return _module;
    }

    /**
     * Get the implicit join path to the <code>oagi.namespace</code> table.
     */
    public Namespace namespace() {
        if (_namespace == null)
            _namespace = new Namespace(this, Keys.MODULE_NAMESPACE_ID_FK);

        return _namespace;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_created_by_fk</code> key.
     */
    public AppUser moduleCreatedByFk() {
        if (_moduleCreatedByFk == null)
            _moduleCreatedByFk = new AppUser(this, Keys.MODULE_CREATED_BY_FK);

        return _moduleCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_last_updated_by_fk</code> key.
     */
    public AppUser moduleLastUpdatedByFk() {
        if (_moduleLastUpdatedByFk == null)
            _moduleLastUpdatedByFk = new AppUser(this, Keys.MODULE_LAST_UPDATED_BY_FK);

        return _moduleLastUpdatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_owner_user_id_fk</code> key.
     */
    public AppUser moduleOwnerUserIdFk() {
        if (_moduleOwnerUserIdFk == null)
            _moduleOwnerUserIdFk = new AppUser(this, Keys.MODULE_OWNER_USER_ID_FK);

        return _moduleOwnerUserIdFk;
    }

    @Override
    public Module as(String alias) {
        return new Module(DSL.name(alias), this);
    }

    @Override
    public Module as(Name alias) {
        return new Module(alias, this);
    }

    @Override
    public Module as(Table<?> alias) {
        return new Module(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Module rename(String name) {
        return new Module(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Module rename(Name name) {
        return new Module(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Module rename(Table<?> name) {
        return new Module(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row13 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row13<ULong, ULong, ULong, String, String, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function13<? super ULong, ? super ULong, ? super ULong, ? super String, ? super String, ? super String, ? super ULong, ? super String, ? super ULong, ? super ULong, ? super ULong, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link #convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function13<? super ULong, ? super ULong, ? super ULong, ? super String, ? super String, ? super String, ? super ULong, ? super String, ? super ULong, ? super ULong, ? super ULong, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
