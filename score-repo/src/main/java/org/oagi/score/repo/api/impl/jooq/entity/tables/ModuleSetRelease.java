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
import org.jooq.Function10;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
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
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.ModuleSetReleaseRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleSetRelease extends TableImpl<ModuleSetReleaseRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>oagi.module_set_release</code>
     */
    public static final ModuleSetRelease MODULE_SET_RELEASE = new ModuleSetRelease();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleSetReleaseRecord> getRecordType() {
        return ModuleSetReleaseRecord.class;
    }

    /**
     * The column <code>oagi.module_set_release.module_set_release_id</code>.
     * Primary, internal database key.
     */
    public final TableField<ModuleSetReleaseRecord, String> MODULE_SET_RELEASE_ID = createField(DSL.name("module_set_release_id"), SQLDataType.CHAR(36).nullable(false), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.module_set_release.module_set_id</code>. A foreign
     * key of the module set.
     */
    public final TableField<ModuleSetReleaseRecord, String> MODULE_SET_ID = createField(DSL.name("module_set_id"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key of the module set.");

    /**
     * The column <code>oagi.module_set_release.release_id</code>. A foreign key
     * of the release.
     */
    public final TableField<ModuleSetReleaseRecord, String> RELEASE_ID = createField(DSL.name("release_id"), SQLDataType.CHAR(36).nullable(false), this, "A foreign key of the release.");

    /**
     * The column <code>oagi.module_set_release.name</code>. This is the name of
     * the module set release.
     */
    public final TableField<ModuleSetReleaseRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(100).nullable(false), this, "This is the name of the module set release.");

    /**
     * The column <code>oagi.module_set_release.description</code>. Description
     * or explanation about the module set release.
     */
    public final TableField<ModuleSetReleaseRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "Description or explanation about the module set release.");

    /**
     * The column <code>oagi.module_set_release.is_default</code>. It would be a
     * default module set if this indicator is checked. Otherwise, it would be
     * an optional.
     */
    public final TableField<ModuleSetReleaseRecord, Byte> IS_DEFAULT = createField(DSL.name("is_default"), SQLDataType.TINYINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.TINYINT)), this, "It would be a default module set if this indicator is checked. Otherwise, it would be an optional.");

    /**
     * The column <code>oagi.module_set_release.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created this
     * MODULE_SET_RELEASE.
     */
    public final TableField<ModuleSetReleaseRecord, String> CREATED_BY = createField(DSL.name("created_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_RELEASE.");

    /**
     * The column <code>oagi.module_set_release.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public final TableField<ModuleSetReleaseRecord, String> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), SQLDataType.CHAR(36).nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_set_release.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public final TableField<ModuleSetReleaseRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_set_release.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public final TableField<ModuleSetReleaseRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "The timestamp when the record was last updated.");

    private ModuleSetRelease(Name alias, Table<ModuleSetReleaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleSetRelease(Name alias, Table<ModuleSetReleaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>oagi.module_set_release</code> table reference
     */
    public ModuleSetRelease(String alias) {
        this(DSL.name(alias), MODULE_SET_RELEASE);
    }

    /**
     * Create an aliased <code>oagi.module_set_release</code> table reference
     */
    public ModuleSetRelease(Name alias) {
        this(alias, MODULE_SET_RELEASE);
    }

    /**
     * Create a <code>oagi.module_set_release</code> table reference
     */
    public ModuleSetRelease() {
        this(DSL.name("module_set_release"), null);
    }

    public <O extends Record> ModuleSetRelease(Table<O> child, ForeignKey<O, ModuleSetReleaseRecord> key) {
        super(child, key, MODULE_SET_RELEASE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Oagi.OAGI;
    }

    @Override
    public UniqueKey<ModuleSetReleaseRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_SET_RELEASE_PRIMARY;
    }

    @Override
    public List<ForeignKey<ModuleSetReleaseRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MODULE_SET_RELEASE_MODULE_SET_ID_FK, Keys.MODULE_SET_RELEASE_RELEASE_ID_FK, Keys.MODULE_SET_RELEASE_CREATED_BY_FK, Keys.MODULE_SET_RELEASE_LAST_UPDATED_BY_FK);
    }

    private transient ModuleSet _moduleSet;
    private transient Release _release;
    private transient AppUser _moduleSetReleaseCreatedByFk;
    private transient AppUser _moduleSetReleaseLastUpdatedByFk;

    /**
     * Get the implicit join path to the <code>oagi.module_set</code> table.
     */
    public ModuleSet moduleSet() {
        if (_moduleSet == null)
            _moduleSet = new ModuleSet(this, Keys.MODULE_SET_RELEASE_MODULE_SET_ID_FK);

        return _moduleSet;
    }

    /**
     * Get the implicit join path to the <code>oagi.release</code> table.
     */
    public Release release() {
        if (_release == null)
            _release = new Release(this, Keys.MODULE_SET_RELEASE_RELEASE_ID_FK);

        return _release;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_set_release_created_by_fk</code> key.
     */
    public AppUser moduleSetReleaseCreatedByFk() {
        if (_moduleSetReleaseCreatedByFk == null)
            _moduleSetReleaseCreatedByFk = new AppUser(this, Keys.MODULE_SET_RELEASE_CREATED_BY_FK);

        return _moduleSetReleaseCreatedByFk;
    }

    /**
     * Get the implicit join path to the <code>oagi.app_user</code> table, via
     * the <code>module_set_release_last_updated_by_fk</code> key.
     */
    public AppUser moduleSetReleaseLastUpdatedByFk() {
        if (_moduleSetReleaseLastUpdatedByFk == null)
            _moduleSetReleaseLastUpdatedByFk = new AppUser(this, Keys.MODULE_SET_RELEASE_LAST_UPDATED_BY_FK);

        return _moduleSetReleaseLastUpdatedByFk;
    }

    @Override
    public ModuleSetRelease as(String alias) {
        return new ModuleSetRelease(DSL.name(alias), this);
    }

    @Override
    public ModuleSetRelease as(Name alias) {
        return new ModuleSetRelease(alias, this);
    }

    @Override
    public ModuleSetRelease as(Table<?> alias) {
        return new ModuleSetRelease(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleSetRelease rename(String name) {
        return new ModuleSetRelease(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleSetRelease rename(Name name) {
        return new ModuleSetRelease(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleSetRelease rename(Table<?> name) {
        return new ModuleSetRelease(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, Byte, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super Byte, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
