/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.ModuleSet;

import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleSetRecord extends UpdatableRecordImpl<ModuleSetRecord> implements Record8<ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = -870762235;

    /**
     * Setter for <code>oagi.module_set.module_set_id</code>. Primary key.
     */
    public void setModuleSetId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_set.module_set_id</code>. Primary key.
     */
    public ULong getModuleSetId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_set.guid</code>. A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.
     */
    public void setGuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_set.guid</code>. A globally unique identifier (GUID) of an SC. Per OAGIS, a GUID is of the form "oagis-id-" followed by a 32 Hex character sequence. Note that each SC is considered intrinsic to each DT, so a SC has a different GUID from the based SC, i.e., SC inherited from the based DT has a new, different GUID.
     */
    public String getGuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>oagi.module_set.name</code>. This is the name of the module set.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_set.name</code>. This is the name of the module set.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>oagi.module_set.description</code>. Description or explanation about the module set or use of the module set.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_set.description</code>. Description or explanation about the module set or use of the module set.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>oagi.module_set.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_set.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this MODULE_SET.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.module_set.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_set.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.module_set.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_set.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_set.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_set.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public LocalDateTime getLastUpdateTimestamp() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, String, String, String, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
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
    public Field<ULong> field5() {
        return ModuleSet.MODULE_SET.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
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
    public ULong component1() {
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
    public ULong component5() {
        return getCreatedBy();
    }

    @Override
    public ULong component6() {
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
    public ULong value1() {
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
    public ULong value5() {
        return getCreatedBy();
    }

    @Override
    public ULong value6() {
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
    public ModuleSetRecord value1(ULong value) {
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
    public ModuleSetRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleSetRecord value6(ULong value) {
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
    public ModuleSetRecord values(ULong value1, String value2, String value3, String value4, ULong value5, ULong value6, LocalDateTime value7, LocalDateTime value8) {
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
    public ModuleSetRecord(ULong moduleSetId, String guid, String name, String description, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleSet.MODULE_SET);

        set(0, moduleSetId);
        set(1, guid);
        set(2, name);
        set(3, description);
        set(4, createdBy);
        set(5, lastUpdatedBy);
        set(6, creationTimestamp);
        set(7, lastUpdateTimestamp);
    }
}
