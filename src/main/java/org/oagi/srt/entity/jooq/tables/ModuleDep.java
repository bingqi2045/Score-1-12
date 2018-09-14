/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
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
import org.oagi.srt.entity.jooq.tables.records.ModuleDepRecord;


/**
 * This table carries the dependency between modules in the MODULE table.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDep extends TableImpl<ModuleDepRecord> {

    private static final long serialVersionUID = -196970017;

    /**
     * The reference instance of <code>oagi.module_dep</code>
     */
    public static final ModuleDep MODULE_DEP = new ModuleDep();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ModuleDepRecord> getRecordType() {
        return ModuleDepRecord.class;
    }

    /**
     * The column <code>oagi.module_dep.module_dep_id</code>. Primary, internal database key.
     */
    public final TableField<ModuleDepRecord, ULong> MODULE_DEP_ID = createField("module_dep_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "Primary, internal database key.");

    /**
     * The column <code>oagi.module_dep.dependency_type</code>. This is a code list. The value tells the expression generator what to do based on this dependency type. 0 = xsd:include, 1 = xsd:import. There could be other values supporting other expressions/syntaxes.
     */
    public final TableField<ModuleDepRecord, Integer> DEPENDENCY_TYPE = createField("dependency_type", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "This is a code list. The value tells the expression generator what to do based on this dependency type. 0 = xsd:include, 1 = xsd:import. There could be other values supporting other expressions/syntaxes.");

    /**
     * The column <code>oagi.module_dep.depending_module_id</code>. Foreign key to the MODULE table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.
     */
    public final TableField<ModuleDepRecord, ULong> DEPENDING_MODULE_ID = createField("depending_module_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the MODULE table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.");

    /**
     * The column <code>oagi.module_dep.depended_module_id</code>. Foreign key to the MODULE table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.
     */
    public final TableField<ModuleDepRecord, ULong> DEPENDED_MODULE_ID = createField("depended_module_id", org.jooq.impl.SQLDataType.BIGINTUNSIGNED.nullable(false), this, "Foreign key to the MODULE table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.");

    /**
     * Create a <code>oagi.module_dep</code> table reference
     */
    public ModuleDep() {
        this(DSL.name("module_dep"), null);
    }

    /**
     * Create an aliased <code>oagi.module_dep</code> table reference
     */
    public ModuleDep(String alias) {
        this(DSL.name(alias), MODULE_DEP);
    }

    /**
     * Create an aliased <code>oagi.module_dep</code> table reference
     */
    public ModuleDep(Name alias) {
        this(alias, MODULE_DEP);
    }

    private ModuleDep(Name alias, Table<ModuleDepRecord> aliased) {
        this(alias, aliased, null);
    }

    private ModuleDep(Name alias, Table<ModuleDepRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table carries the dependency between modules in the MODULE table."));
    }

    public <O extends Record> ModuleDep(Table<O> child, ForeignKey<O, ModuleDepRecord> key) {
        super(child, key, MODULE_DEP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Oagi.OAGI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MODULE_DEP_MODULE_DEP_DEPENDED_MODULE_ID_FK, Indexes.MODULE_DEP_MODULE_DEP_DEPENDING_MODULE_ID_FK, Indexes.MODULE_DEP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ModuleDepRecord, ULong> getIdentity() {
        return Keys.IDENTITY_MODULE_DEP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ModuleDepRecord> getPrimaryKey() {
        return Keys.KEY_MODULE_DEP_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ModuleDepRecord>> getKeys() {
        return Arrays.<UniqueKey<ModuleDepRecord>>asList(Keys.KEY_MODULE_DEP_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ModuleDepRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ModuleDepRecord, ?>>asList(Keys.MODULE_DEP_DEPENDING_MODULE_ID_FK, Keys.MODULE_DEP_DEPENDED_MODULE_ID_FK);
    }

    public Module moduleDepDependingModuleIdFk() {
        return new Module(this, Keys.MODULE_DEP_DEPENDING_MODULE_ID_FK);
    }

    public Module moduleDepDependedModuleIdFk() {
        return new Module(this, Keys.MODULE_DEP_DEPENDED_MODULE_ID_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleDep as(String alias) {
        return new ModuleDep(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleDep as(Name alias) {
        return new ModuleDep(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDep rename(String name) {
        return new ModuleDep(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ModuleDep rename(Name name) {
        return new ModuleDep(name, null);
    }
}
