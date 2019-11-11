/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.ModuleDep;


/**
 * This table carries the dependency between modules in the MODULE table.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDepRecord extends UpdatableRecordImpl<ModuleDepRecord> implements Record4<ULong, Integer, ULong, ULong> {

    private static final long serialVersionUID = 458420505;

    /**
     * Setter for <code>oagi.module_dep.module_dep_id</code>. Primary, internal database key.
     */
    public void setModuleDepId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_dep.module_dep_id</code>. Primary, internal database key.
     */
    public ULong getModuleDepId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_dep.dependency_type</code>. This is a code list. The value tells the expression generator what to do based on this dependency type. 0 = xsd:include, 1 = xsd:import. There could be other values supporting other expressions/syntaxes.
     */
    public void setDependencyType(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_dep.dependency_type</code>. This is a code list. The value tells the expression generator what to do based on this dependency type. 0 = xsd:include, 1 = xsd:import. There could be other values supporting other expressions/syntaxes.
     */
    public Integer getDependencyType() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>oagi.module_dep.depending_module_id</code>. Foreign key to the MODULE table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.
     */
    public void setDependingModuleId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_dep.depending_module_id</code>. Foreign key to the MODULE table. It identifies a depending module. For example, in XML schema if module A imports or includes module B, then module A is a depending module.
     */
    public ULong getDependingModuleId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_dep.depended_module_id</code>. Foreign key to the MODULE table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.
     */
    public void setDependedModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_dep.depended_module_id</code>. Foreign key to the MODULE table. It identifies a depended module counterpart of the depending module. For example, in XML schema if module A imports or includes module B, then module B is a depended module.
     */
    public ULong getDependedModuleId() {
        return (ULong) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, Integer, ULong, ULong> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, Integer, ULong, ULong> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return ModuleDep.MODULE_DEP.MODULE_DEP_ID;
    }

    @Override
    public Field<Integer> field2() {
        return ModuleDep.MODULE_DEP.DEPENDENCY_TYPE;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleDep.MODULE_DEP.DEPENDING_MODULE_ID;
    }

    @Override
    public Field<ULong> field4() {
        return ModuleDep.MODULE_DEP.DEPENDED_MODULE_ID;
    }

    @Override
    public ULong component1() {
        return getModuleDepId();
    }

    @Override
    public Integer component2() {
        return getDependencyType();
    }

    @Override
    public ULong component3() {
        return getDependingModuleId();
    }

    @Override
    public ULong component4() {
        return getDependedModuleId();
    }

    @Override
    public ULong value1() {
        return getModuleDepId();
    }

    @Override
    public Integer value2() {
        return getDependencyType();
    }

    @Override
    public ULong value3() {
        return getDependingModuleId();
    }

    @Override
    public ULong value4() {
        return getDependedModuleId();
    }

    @Override
    public ModuleDepRecord value1(ULong value) {
        setModuleDepId(value);
        return this;
    }

    @Override
    public ModuleDepRecord value2(Integer value) {
        setDependencyType(value);
        return this;
    }

    @Override
    public ModuleDepRecord value3(ULong value) {
        setDependingModuleId(value);
        return this;
    }

    @Override
    public ModuleDepRecord value4(ULong value) {
        setDependedModuleId(value);
        return this;
    }

    @Override
    public ModuleDepRecord values(ULong value1, Integer value2, ULong value3, ULong value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ModuleDepRecord
     */
    public ModuleDepRecord() {
        super(ModuleDep.MODULE_DEP);
    }

    /**
     * Create a detached, initialised ModuleDepRecord
     */
    public ModuleDepRecord(ULong moduleDepId, Integer dependencyType, ULong dependingModuleId, ULong dependedModuleId) {
        super(ModuleDep.MODULE_DEP);

        set(0, moduleDepId);
        set(1, dependencyType);
        set(2, dependingModuleId);
        set(3, dependedModuleId);
    }
}
