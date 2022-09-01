/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record13;
import org.jooq.Row13;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Module;


/**
 * The module table stores information about a physical file, into which CC
 * components will be generated during the expression generation.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleRecord extends UpdatableRecordImpl<ModuleRecord> implements Record13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.module.module_id</code>. Primary, internal database
     * key.
     */
    public void setModuleId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module.module_id</code>. Primary, internal database
     * key.
     */
    public String getModuleId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.module.module_set_id</code>. This indicates a
     * module set.
     */
    public void setModuleSetId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module.module_set_id</code>. This indicates a
     * module set.
     */
    public String getModuleSetId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.module.parent_module_id</code>. This indicates a
     * parent module id. root module will be NULL.
     */
    public void setParentModuleId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module.parent_module_id</code>. This indicates a
     * parent module id. root module will be NULL.
     */
    public String getParentModuleId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.module.type</code>. This is a type column for
     * indicates module is FILE or DIRECTORY.
     */
    public void setType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module.type</code>. This is a type column for
     * indicates module is FILE or DIRECTORY.
     */
    public String getType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.module.path</code>. Absolute path to the module.
     */
    public void setPath(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module.path</code>. Absolute path to the module.
     */
    public String getPath() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.module.name</code>. The is the filename of the
     * module. The reason to not including the extension is that the extension
     * maybe dependent on the expression. For XML schema, '.xsd' maybe added; or
     * for JSON, '.json' maybe added as the file extension.
     */
    public void setName(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module.name</code>. The is the filename of the
     * module. The reason to not including the extension is that the extension
     * maybe dependent on the expression. For XML schema, '.xsd' maybe added; or
     * for JSON, '.json' maybe added as the file extension.
     */
    public String getName() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.module.namespace_id</code>. Foreign key to the
     * NAMESPACE table.
     */
    public void setNamespaceId(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module.namespace_id</code>. Foreign key to the
     * NAMESPACE table.
     */
    public String getNamespaceId() {
        return (String) get(6);
    }

    /**
     * Setter for <code>oagi.module.version_num</code>. This is the version
     * number to be assigned to the schema module.
     */
    public void setVersionNum(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module.version_num</code>. This is the version
     * number to be assigned to the schema module.
     */
    public String getVersionNum() {
        return (String) get(7);
    }

    /**
     * Setter for <code>oagi.module.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this MODULE.
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>oagi.module.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this MODULE.
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>oagi.module.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public void setLastUpdatedBy(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>oagi.module.last_updated_by</code>. Foreign key to the
     * APP_USER table referring to the last user who updated the record. 
     * 
     * In the history record, this should always be the user who is editing the
     * entity (perhaps except when the ownership has just been changed).
     */
    public String getLastUpdatedBy() {
        return (String) get(9);
    }

    /**
     * Setter for <code>oagi.module.owner_user_id</code>. Foreign key to the
     * APP_USER table identifying the user who can update or delete the record.
     */
    public void setOwnerUserId(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>oagi.module.owner_user_id</code>. Foreign key to the
     * APP_USER table identifying the user who can update or delete the record.
     */
    public String getOwnerUserId() {
        return (String) get(10);
    }

    /**
     * Setter for <code>oagi.module.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>oagi.module.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>oagi.module.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(12, value);
    }

    /**
     * Getter for <code>oagi.module.last_update_timestamp</code>. The timestamp
     * when the record was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record13 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row13) super.fieldsRow();
    }

    @Override
    public Row13<String, String, String, String, String, String, String, String, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row13) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Module.MODULE.MODULE_ID;
    }

    @Override
    public Field<String> field2() {
        return Module.MODULE.MODULE_SET_ID;
    }

    @Override
    public Field<String> field3() {
        return Module.MODULE.PARENT_MODULE_ID;
    }

    @Override
    public Field<String> field4() {
        return Module.MODULE.TYPE;
    }

    @Override
    public Field<String> field5() {
        return Module.MODULE.PATH;
    }

    @Override
    public Field<String> field6() {
        return Module.MODULE.NAME;
    }

    @Override
    public Field<String> field7() {
        return Module.MODULE.NAMESPACE_ID;
    }

    @Override
    public Field<String> field8() {
        return Module.MODULE.VERSION_NUM;
    }

    @Override
    public Field<String> field9() {
        return Module.MODULE.CREATED_BY;
    }

    @Override
    public Field<String> field10() {
        return Module.MODULE.LAST_UPDATED_BY;
    }

    @Override
    public Field<String> field11() {
        return Module.MODULE.OWNER_USER_ID;
    }

    @Override
    public Field<LocalDateTime> field12() {
        return Module.MODULE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field13() {
        return Module.MODULE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getModuleId();
    }

    @Override
    public String component2() {
        return getModuleSetId();
    }

    @Override
    public String component3() {
        return getParentModuleId();
    }

    @Override
    public String component4() {
        return getType();
    }

    @Override
    public String component5() {
        return getPath();
    }

    @Override
    public String component6() {
        return getName();
    }

    @Override
    public String component7() {
        return getNamespaceId();
    }

    @Override
    public String component8() {
        return getVersionNum();
    }

    @Override
    public String component9() {
        return getCreatedBy();
    }

    @Override
    public String component10() {
        return getLastUpdatedBy();
    }

    @Override
    public String component11() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime component12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value1() {
        return getModuleId();
    }

    @Override
    public String value2() {
        return getModuleSetId();
    }

    @Override
    public String value3() {
        return getParentModuleId();
    }

    @Override
    public String value4() {
        return getType();
    }

    @Override
    public String value5() {
        return getPath();
    }

    @Override
    public String value6() {
        return getName();
    }

    @Override
    public String value7() {
        return getNamespaceId();
    }

    @Override
    public String value8() {
        return getVersionNum();
    }

    @Override
    public String value9() {
        return getCreatedBy();
    }

    @Override
    public String value10() {
        return getLastUpdatedBy();
    }

    @Override
    public String value11() {
        return getOwnerUserId();
    }

    @Override
    public LocalDateTime value12() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value13() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ModuleRecord value1(String value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleRecord value2(String value) {
        setModuleSetId(value);
        return this;
    }

    @Override
    public ModuleRecord value3(String value) {
        setParentModuleId(value);
        return this;
    }

    @Override
    public ModuleRecord value4(String value) {
        setType(value);
        return this;
    }

    @Override
    public ModuleRecord value5(String value) {
        setPath(value);
        return this;
    }

    @Override
    public ModuleRecord value6(String value) {
        setName(value);
        return this;
    }

    @Override
    public ModuleRecord value7(String value) {
        setNamespaceId(value);
        return this;
    }

    @Override
    public ModuleRecord value8(String value) {
        setVersionNum(value);
        return this;
    }

    @Override
    public ModuleRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleRecord value10(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleRecord value11(String value) {
        setOwnerUserId(value);
        return this;
    }

    @Override
    public ModuleRecord value12(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleRecord value13(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleRecord values(String value1, String value2, String value3, String value4, String value5, String value6, String value7, String value8, String value9, String value10, String value11, LocalDateTime value12, LocalDateTime value13) {
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
        value12(value12);
        value13(value13);
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
    public ModuleRecord(String moduleId, String moduleSetId, String parentModuleId, String type, String path, String name, String namespaceId, String versionNum, String createdBy, String lastUpdatedBy, String ownerUserId, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(Module.MODULE);

        setModuleId(moduleId);
        setModuleSetId(moduleSetId);
        setParentModuleId(parentModuleId);
        setType(type);
        setPath(path);
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
