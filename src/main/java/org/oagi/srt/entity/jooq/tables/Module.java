/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row10;
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
import org.oagi.srt.entity.jooq.tables.records.ModuleRecord;


/**
 * The module table stores information about a physical file, into which CC 
 * components will be generated during the expression generation.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Module extends TableImpl<ModuleRecord> {

    private static final long serialVersionUID = -859490477;

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
     * The column <code>oagi.module.module_id</code>. Primary, internal database key.
     */
    public final TableField<ModuleRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.module.module</code>. The is the subdirectory and filename. The format is Windows file path. The starting directory typically is the root folder of all the release content. For example, for OAGIS 10.1 Model, the root directory is Model. If the file shall be directly under the Model directory, then this column should be 'Model\filename' without the extension. If the file is under, say, Model\Platform\2_1\Common\Components directory, then the value of this column shall be 'Model\Platform\2_1\Common\Components\filenam'. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, '.xsd' maybe added; or for JSON, '.json' maybe added as the file extension.
     */
    public final TableField<ModuleRecord, String> MODULE_ = createField(DSL.name("module"), org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false), this, "The is the subdirectory and filename. The format is Windows file path. The starting directory typically is the root folder of all the release content. For example, for OAGIS 10.1 Model, the root directory is Model. If the file shall be directly under the Model directory, then this column should be 'Model\\filename' without the extension. If the file is under, say, Model\\Platform\\2_1\\Common\\Components directory, then the value of this column shall be 'Model\\Platform\\2_1\\Common\\Components\\filenam'. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, '.xsd' maybe added; or for JSON, '.json' maybe added as the file extension.");

    /**
     * The column <code>oagi.module.release_id</code>. Foreign key to the RELEASE table. It identifies the release, for which this module is associated.
     */
    public final TableField<ModuleRecord, ULong> RELEASE_ID = createField(DSL.name("release_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the RELEASE table. It identifies the release, for which this module is associated.");

    /**
     * The column <code>oagi.module.namespace_id</code>. Note that a release record has a namespace associated. The NAMESPACE_ID, if specified here, overrides the release's namespace. However, the NAMESPACE_ID associated with the component takes the highest precedence.
     */
    public final TableField<ModuleRecord, ULong> NAMESPACE_ID = createField(DSL.name("namespace_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Note that a release record has a namespace associated. The NAMESPACE_ID, if specified here, overrides the release's namespace. However, the NAMESPACE_ID associated with the component takes the highest precedence.");

    /**
     * The column <code>oagi.module.version_num</code>. This is the version number to be assigned to the schema module.
     */
    public final TableField<ModuleRecord, String> VERSION_NUM = createField(DSL.name("version_num"), org.jooq.impl.SQLDataType.VARCHAR(45), this, "This is the version number to be assigned to the schema module.");

    /**
     * The column <code>oagi.module.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE.
     */
    public final TableField<ModuleRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this MODULE.");

    /**
     * The column <code>oagi.module.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public final TableField<ModuleRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record. \n\nIn the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).");

    /**
     * The column <code>oagi.module.owner_user_id</code>. Foreign key to the APP_USER table identifying the user who can update or delete the record.
     */
    public final TableField<ModuleRecord, ULong> OWNER_USER_ID = createField(DSL.name("owner_user_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table identifying the user who can update or delete the record.");

    /**
     * The column <code>oagi.module.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleRecord, Timestamp> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleRecord, Timestamp> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module</code> table reference
     */
    public Module() {
        this(DSL.name("module"), null);
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

    private Module(Name alias, Table<ModuleRecord> aliased) {
        this(alias, aliased, null);
    }

    private Module(Name alias, Table<ModuleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The module table stores information about a physical file, into which CC components will be generated during the expression generation."));
    }

    public <O extends Record> Module(Table<O> child, ForeignKey<O, ModuleRecord> key) {
        super(child, key, MODULE);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MODULE_MODULE_CREATED_BY_FK, Indexes.MODULE_MODULE_LAST_UPDATED_BY_FK, Indexes.MODULE_MODULE_NAMESPACE_ID_FK, Indexes.MODULE_MODULE_OWNER_USER_ID_FK, Indexes.MODULE_MODULE_RELEASE_ID_FK, Indexes.MODULE_PRIMARY);
    }

    @Override
    public Identity<ModuleRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE;
    }

    @Override
    public UniqueKey<ModuleRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleRecord>>asList(Keys.KEY_MODULE_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleRecord, ?>>asList(Keys.MODULE_RELEASE_ID_FK, Keys.MODULE_NAMESPACE_ID_FK, Keys.MODULE_CREATED_BY_FK, Keys.MODULE_LAST_UPDATED_BY_FK, Keys.MODULE_OWNER_USER_ID_FK);
    }

    public Release release() {
        return new Release(this, Keys.MODULE_RELEASE_ID_FK);
    }

    public Namespace namespace() {
        return new Namespace(this, Keys.MODULE_NAMESPACE_ID_FK);
    }

    public AppUser moduleCreatedByFk() {
        return new AppUser(this, Keys.MODULE_CREATED_BY_FK);
    }

    public AppUser moduleLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_LAST_UPDATED_BY_FK);
    }

    public AppUser moduleOwnerUserIdFk() {
        return new AppUser(this, Keys.MODULE_OWNER_USER_ID_FK);
    }

    @Override
    public Module as(String alias) {
        return new Module(DSL.name(alias), this);
    }

    @Override
    public Module as(Name alias) {
        return new Module(alias, this);
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

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<ULong, String, ULong, ULong, String, ULong, ULong, ULong, Timestamp, Timestamp> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
