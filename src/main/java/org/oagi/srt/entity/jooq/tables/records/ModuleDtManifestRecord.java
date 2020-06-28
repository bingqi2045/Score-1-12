/*
 * This file is generated by jOOQ.
 */
package org.oagi.srt.entity.jooq.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.ModuleDtManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleDtManifestRecord extends UpdatableRecordImpl<ModuleDtManifestRecord> implements Record8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = -1461468318;

    /**
     * Setter for <code>oagi.module_dt_manifest.module_dt_manifest_id</code>. Primary key.
     */
    public void setModuleDtManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.module_dt_manifest_id</code>. Primary key.
     */
    public ULong getModuleDtManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public void setModuleSetReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public ULong getModuleSetReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.dt_manifest_id</code>. A foreign key of the dt manifest record.
     */
    public void setDtManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.dt_manifest_id</code>. A foreign key of the dt manifest record.
     */
    public ULong getDtManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.module_id</code>. A foreign key of the module record.
     */
    public void setModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.module_id</code>. A foreign key of the module record.
     */
    public ULong getModuleId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_dt_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_dt_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
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
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.MODULE_DT_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.MODULE_SET_RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.DT_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<ULong> field5() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleDtManifest.MODULE_DT_MANIFEST.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleDtManifestId();
    }

    @Override
    public ULong component2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong component3() {
        return getDtManifestId();
    }

    @Override
    public ULong component4() {
        return getModuleId();
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
        return getModuleDtManifestId();
    }

    @Override
    public ULong value2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong value3() {
        return getDtManifestId();
    }

    @Override
    public ULong value4() {
        return getModuleId();
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
    public ModuleDtManifestRecord value1(ULong value) {
        setModuleDtManifestId(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value2(ULong value) {
        setModuleSetReleaseId(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value3(ULong value) {
        setDtManifestId(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value4(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value6(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleDtManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, ULong value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached ModuleDtManifestRecord
     */
    public ModuleDtManifestRecord() {
        super(ModuleDtManifest.MODULE_DT_MANIFEST);
    }

    /**
     * Create a detached, initialised ModuleDtManifestRecord
     */
    public ModuleDtManifestRecord(ULong moduleDtManifestId, ULong moduleSetReleaseId, ULong dtManifestId, ULong moduleId, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleDtManifest.MODULE_DT_MANIFEST);

        set(0, moduleDtManifestId);
        set(1, moduleSetReleaseId);
        set(2, dtManifestId);
        set(3, moduleId);
        set(4, createdBy);
        set(5, lastUpdatedBy);
        set(6, creationTimestamp);
        set(7, lastUpdateTimestamp);
    }
}
