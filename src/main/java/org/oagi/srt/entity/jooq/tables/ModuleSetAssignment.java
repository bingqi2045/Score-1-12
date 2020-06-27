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
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.Keys;
import org.oagi.srt.entity.jooq.Oagi;
import org.oagi.srt.entity.jooq.tables.records.ModuleSetAssignmentRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleSetAssignment extends TableImpl<ModuleSetAssignmentRecord> {

    private static final long serialVersionUID = 1291870576;

    /**
     * The reference instance of <code>oagi.module_set_assignment</code>
     */
    public static final ModuleSetAssignment MODULE_SET_ASSIGNMENT = new ModuleSetAssignment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleSetAssignmentRecord> getRecordType() {
        return ModuleSetAssignmentRecord.class;
    }

    /**
     * The column <code>oagi.module_set_assignment.module_set_assignment_id</code>. Primary key.
     */
    public final TableField<ModuleSetAssignmentRecord, ULong> MODULE_SET_ASSIGNMENT_ID = createField(DSL.name("module_set_assignment_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary key.");

    /**
     * The column <code>oagi.module_set_assignment.module_set_id</code>. A foreign key of the module set.
     */
    public final TableField<ModuleSetAssignmentRecord, ULong> MODULE_SET_ID = createField(DSL.name("module_set_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module set.");

    /**
     * The column <code>oagi.module_set_assignment.module_id</code>. A foreign key of the module assigned in the module set.
     */
    public final TableField<ModuleSetAssignmentRecord, ULong> MODULE_ID = createField(DSL.name("module_id"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "A foreign key of the module assigned in the module set.");

    /**
     * The column <code>oagi.module_set_assignment.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_ASSIGNMENT.
     */
    public final TableField<ModuleSetAssignmentRecord, ULong> CREATED_BY = createField(DSL.name("created_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET_ASSIGNMENT.");

    /**
     * The column <code>oagi.module_set_assignment.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public final TableField<ModuleSetAssignmentRecord, ULong> LAST_UPDATED_BY = createField(DSL.name("last_updated_by"), org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the APP_USER table referring to the last user who updated the record.");

    /**
     * The column <code>oagi.module_set_assignment.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public final TableField<ModuleSetAssignmentRecord, LocalDateTime> CREATION_TIMESTAMP = createField(DSL.name("creation_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was first created.");

    /**
     * The column <code>oagi.module_set_assignment.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public final TableField<ModuleSetAssignmentRecord, LocalDateTime> LAST_UPDATE_TIMESTAMP = createField(DSL.name("last_update_timestamp"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "The timestamp when the record was last updated.");

    /**
     * Create a <code>oagi.module_set_assignment</code> table reference
     */
    public ModuleSetAssignment() {
        this(DSL.name("module_set_assignment"), null);
    }

    /**
     * Create an aliased <code>oagi.module_set_assignment</code> table reference
     */
    public ModuleSetAssignment(String alias) {
        this(DSL.name(alias), MODULE_SET_ASSIGNMENT);
    }

    /**
     * Create an aliased <code>oagi.module_set_assignment</code> table reference
     */
    public ModuleSetAssignment(Name alias) {
        this(alias, MODULE_SET_ASSIGNMENT);
    }

    private ModuleSetAssignment(Name alias, Table<ModuleSetAssignmentRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleSetAssignment(Name alias, Table<ModuleSetAssignmentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> ModuleSetAssignment(Table<O> child, ForeignKey<O, ModuleSetAssignmentRecord> key) {
        super(child, key, MODULE_SET_ASSIGNMENT);
    }

    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    @Override
    public Identity<ModuleSetAssignmentRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_SET_ASSIGNMENT;
    }

    @Override
    public UniqueKey<ModuleSetAssignmentRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_SET_ASSIGNMENT_PRIMARY;
    }

    @Override
    public List<UniqueKey<ModuleSetAssignmentRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleSetAssignmentRecord>>asList(Keys.KEY_MODULE_SET_ASSIGNMENT_PRIMARY);
    }

    @Override
    public List<ForeignKey<ModuleSetAssignmentRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleSetAssignmentRecord, ?>>asList(Keys.MODULE_SET_ASSIGNMENT_MODULE_SET_ID_FK, Keys.MODULE_SET_ASSIGNMENT_MODULE_ID_FK, Keys.MODULE_SET_ASSIGNMENT_CREATED_BY_FK, Keys.MODULE_SET_ASSIGNMENT_LAST_UPDATED_BY_FK);
    }

    public ModuleSet moduleSet() {
        return new ModuleSet(this, Keys.MODULE_SET_ASSIGNMENT_MODULE_SET_ID_FK);
    }

    public Module module() {
        return new Module(this, Keys.MODULE_SET_ASSIGNMENT_MODULE_ID_FK);
    }

    public AppUser moduleSetAssignmentCreatedByFk() {
        return new AppUser(this, Keys.MODULE_SET_ASSIGNMENT_CREATED_BY_FK);
    }

    public AppUser moduleSetAssignmentLastUpdatedByFk() {
        return new AppUser(this, Keys.MODULE_SET_ASSIGNMENT_LAST_UPDATED_BY_FK);
    }

    @Override
    public ModuleSetAssignment as(String alias) {
        return new ModuleSetAssignment(DSL.name(alias), this);
    }

    @Override
    public ModuleSetAssignment as(Name alias) {
        return new ModuleSetAssignment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleSetAssignment rename(String name) {
        return new ModuleSetAssignment(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleSetAssignment rename(Name name) {
        return new ModuleSetAssignment(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
