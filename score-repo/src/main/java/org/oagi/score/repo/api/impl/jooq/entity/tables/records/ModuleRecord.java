/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Module;


/**
 * The module table stores information about a physical file, into which CC 
 * components will be generated during the expression generation.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleRecord extends UpdatableRecordImpl<ModuleRecord> implements Record11<ULong, ULong, ULong, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.module.module_id</code>. Primary, internal database key.
     */
    public void setModuleId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module.module_id</code>. Primary, internal database key.
     */
    public ULong getModuleId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module.module_dir_id</code>. This indicates a module directory.
     */
    public void setModuleDirId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module.module_dir_id</code>. This indicates a module directory.
     */
    public ULong getModuleDirId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.module.module_set_id</code>. This indicates a module set.
     */
    public void setModuleSetId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module.module_set_id</code>. This indicates a module set.
     */
    public ULong getModuleSetId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module.name</code>. The is the filename of the module. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, '.xsd' maybe added; or for JSON, '.json' maybe added as the file extension.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module.name</code>. The is the filename of the module. The reason to not including the extension is that the extension maybe dependent on the expression. For XML schema, '.xsd' maybe added; or for JSON, '.json' maybe added as the file extension.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.module.namespace_id</code>. Note that a release record has a namespace associated. The NAMESPACE_ID, if specified here, overrides the release's namespace. However, the NAMESPACE_ID associated with the component takes the highest precedence.
     */
    public void setNamespaceId(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module.namespace_id</code>. Note that a release record has a namespace associated. The NAMESPACE_ID, if specified here, overrides the release's namespace. However, the NAMESPACE_ID associated with the component takes the highest precedence.
     */
    public ULong getNamespaceId() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.module.version_num</code>. This is the version number to be assigned to the schema module.
     */
    public void setVersionNum(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module.version_num</code>. This is the version number to be assigned to the schema module.
     */
    public String getVersionNum() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.module.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE.
     */
    public void setCreatedBy(ULong value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE.
     */
    public ULong getCreatedBy() {
        return (ULong) get(6);
    }

    /**
     * Setter for <code>oagi.module.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(ULong value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record. 

In the history record, this should always be the user who is editing the entity (perhaps except when the ownership has just been changed).
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(7);
    }

    /**
     * Setter for <code>oagi.module.owner_user_id</code>. Foreign key to the APP_USER table identifying the user who can update or delete the record.
     */
    public void setOwnerUserId(ULong value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.module.owner_user_id</code>. Foreign key to the APP_USER table identifying the user who can update or delete the record.
     */
    public ULong getOwnerUserId() {
        return (ULong) get(8);
    }

    /**
     * Setter for <code>oagi.module.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.module.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>oagi.module.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.module.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<ULong, ULong, ULong, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<ULong, ULong, ULong, String, ULong, String, ULong, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Module.MODULE.MODULE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return Module.MODULE.MODULE_DIR_ID;
    }

    @Override
    public Field<ULong> field3() {
        return Module.MODULE.MODULE_SET_ID;
    }

    @Override
    public Field<String> field4() {
        return Module.MODULE.NAME;
    }

    @Override
    public Field<ULong> field5() {
        return Module.MODULE.NAMESPACE_ID;
    }

    @Override
    public Field<String> field6() {
        return Module.MODULE.VERSION_NUM;
    }

    @Override
    public Field<ULong> field7() {
        return Module.MODULE.CREATED_BY;
    }

    @Override
    public Field<ULong> field8() {
        return Module.MODULE.LAST_UPDATED_BY;
    }

    @Override
    public Field<ULong> field9() {
        return Module.MODULE.OWNER_USER_ID;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return Module.MODULE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return Module.MODULE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleId();
    }

    @Override
    public ULong component2() {
        return getModuleDirId();
    }

    @Override
    public ULong component3() {
        return getModuleSetId();
    }

    @Override
    public String component4() {
        return getName();
    }

    @Override
    public ULong component5() {
        return getNamespaceId();
    }

    @Override
    public String component6() {
        return getVersionNum();
    }

    @Override
    public ULong component7() {
        return getCreatedBy();
    }

    @Override
    public ULong component8() {
        return getLastUpdatedBy();
    }

    @Override
    public ULong component9() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime component10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ULong value1() {
        return getModuleId();
    }

    @Override
    public ULong value2() {
        return getModuleDirId();
    }

    @Override
    public ULong value3() {
        return getModuleSetId();
    }

    @Override
    public String value4() {
        return getName();
    }

    @Override
    public ULong value5() {
        return getNamespaceId();
    }

    @Override
    public String value6() {
        return getVersionNum();
    }

    @Override
    public ULong value7() {
        return getCreatedBy();
    }

    @Override
    public ULong value8() {
        return getLastUpdatedBy();
    }

    @Override
    public ULong value9() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime value10() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value11() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ModuleRecord value1(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleRecord value2(ULong value) {
        setModuleDirId(value);
        return this;
    }

    @Override
    public ModuleRecord value3(ULong value) {
        setModuleSetId(value);
        return this;
    }

    @Override
    public ModuleRecord value4(String value) {
        setName(value);
        return this;
    }

    @Override
    public ModuleRecord value5(ULong value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public ModuleRecord value6(String value) {
        setVersionNum(value);
        return this;
    }

    @Override
    public ModuleRecord value7(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleRecord value8(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleRecord value9(ULong value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public ModuleRecord value10(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleRecord value11(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleRecord values(ULong value1, ULong value2, ULong value3, String value4, ULong value5, String value6, ULong value7, ULong value8, ULong value9, LocalDateTime value10, LocalDateTime value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ModuleRecord
     */
    public ModuleRecord() {
        super(Module.MODULE);
    }

    /**
     * Create a detached, initialised ModuleRecord
     */
    public ModuleRecord(ULong moduleId, ULong moduleDirId, ULong moduleSetId, String name, ULong namespaceId, String versionNum, ULong createdBy, ULong lastUpdatedBy, ULong ownerUserId, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(Module.MODULE);

        setModuleId(moduleId);
        setModuleDirId(moduleDirId);
        setModuleSetId(moduleSetId);
        setName(name);
        setNamespaceId(namespaceId);
        setVersionNum(versionNum);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setOwnerUserId(ownerUserId);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
