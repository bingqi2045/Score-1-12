/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.repo.api.impl.jooq.entity.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleSet;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleSetRecord extends UpdatableRecordImpl<ModuleSetRecord> implements Record8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.module_set.module_set_id</code>. Primary, internal
     * database key.
     */
    public void setModuleSetId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_set.module_set_id</code>. Primary, internal
     * database key.
     */
    public String getModuleSetId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>oagi.module_set.guid</code>. A globally unique
     * identifier (GUID).
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_set.guid</code>. A globally unique
     * identifier (GUID).
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.module_set.name</code>. This is the name of the
     * module set.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_set.name</code>. This is the name of the
     * module set.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.module_set.description</code>. Description or
     * explanation about the module set or use of the module set.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_set.description</code>. Description or
     * explanation about the module set or use of the module set.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.module_set.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this MODULE_SET.
     */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_set.created_by</code>. Foreign key to the
     * APP_USER table. It indicates the user who created this MODULE_SET.
     */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.module_set.last_updated_by</code>. Foreign key to
     * the APP_USER table referring to the last user who updated the record.
     */
    public void setLastUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_set.last_updated_by</code>. Foreign key to
     * the APP_USER table referring to the last user who updated the record.
     */
    public String getLastUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.module_set.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_set.creation_timestamp</code>. The timestamp
     * when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_set.last_update_timestamp</code>. The
     * timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_set.last_update_timestamp</code>. The
     * timestamp when the record was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<String, String, String, String, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return ModuleSet.MODULE_SET.MODULE_SET_ID;
    }

    @Override
    public Field<String> field2() {
        return ModuleSet.MODULE_SET.GUID;
    }

    @Override
    public Field<String> field3() {
        return ModuleSet.MODULE_SET.NAME;
    }

    @Override
    public Field<String> field4() {
        return ModuleSet.MODULE_SET.DESCRIPTION;
    }

    @Override
    public Field<String> field5() {
        return ModuleSet.MODULE_SET.CREATED_BY;
    }

    @Override
    public Field<String> field6() {
        return ModuleSet.MODULE_SET.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleSet.MODULE_SET.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleSet.MODULE_SET.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public String component1() {
        return getModuleSetId();
    }

    @Override
    public String component2() {
        return getGuid();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public String component4() {
        return getDescription();
    }

    @Override
    public String component5() {
        return getCreatedBy();
    }

    @Override
    public String component6() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime component7() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime component8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public String value1() {
        return getModuleSetId();
    }

    @Override
    public String value2() {
        return getGuid();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public String value4() {
        return getDescription();
    }

    @Override
    public String value5() {
        return getCreatedBy();
    }

    @Override
    public String value6() {
        return getLastUpdatedBy();
    }

    @Override
    public LocalDateTime value7() {
        return getCreationTimestamp();
    }

    @Override
    public LocalDateTime value8() {
        return getLastUpdateTimestamp();
    }

    @Override
    public ModuleSetRecord value1(String value) {
        setModuleSetId(value);
        return this;
    }

    @Override
    public ModuleSetRecord value2(String value) {
        setGuid(value);
        return this;
    }

    @Override
    public ModuleSetRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public ModuleSetRecord value4(String value) {
        setDescription(value);
        return this;
    }

    @Override
    public ModuleSetRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleSetRecord value6(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleSetRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleSetRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleSetRecord values(String value1, String value2, String value3, String value4, String value5, String value6, LocalDateTime value7, LocalDateTime value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ModuleSetRecord
     */
    public ModuleSetRecord() {
        super(ModuleSet.MODULE_SET);
    }

    /**
     * Create a detached, initialised ModuleSetRecord
     */
    public ModuleSetRecord(String moduleSetId, String guid, String name, String description, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleSet.MODULE_SET);

        setModuleSetId(moduleSetId);
        setGuid(guid);
        setName(name);
        setDescription(description);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
