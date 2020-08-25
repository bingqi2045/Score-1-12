/*
 * This file is generated by jOOQ.
 */
package org.oagi.score.entity.jooq.tables.records;


import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.tables.ModuleBccpManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleBccpManifestRecord extends UpdatableRecordImpl<ModuleBccpManifestRecord> implements Record8<ULong, ULong, ULong, ULong, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 153535426;

    /**
     * Setter for <code>oagi.module_bccp_manifest.module_bccp_manifest_id</code>. Primary key.
     */
    public void setModuleBccpManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.module_bccp_manifest_id</code>. Primary key.
     */
    public ULong getModuleBccpManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public void setModuleSetReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.module_set_release_id</code>. A foreign key of the module set release record.
     */
    public ULong getModuleSetReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.bccp_manifest_id</code>. A foreign key of the bccp manifest record.
     */
    public void setBccpManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.bccp_manifest_id</code>. A foreign key of the bccp manifest record.
     */
    public ULong getBccpManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.module_id</code>. A foreign key of the module record.
     */
    public void setModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.module_id</code>. A foreign key of the module record.
     */
    public ULong getModuleId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.created_by</code>. Foreign key to the APP_USER table. It indicates the user who created this record.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.last_updated_by</code>. Foreign key to the APP_USER table referring to the last user who updated the record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.creation_timestamp</code>. The timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_bccp_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_bccp_manifest.last_update_timestamp</code>. The timestamp when the record was last updated.
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
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.MODULE_BCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.MODULE_SET_RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.BCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<ULong> field5() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleBccpManifest.MODULE_BCCP_MANIFEST.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleBccpManifestId();
    }

    @Override
    public ULong component2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong component3() {
        return getBccpManifestId();
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
        return getModuleBccpManifestId();
    }

    @Override
    public ULong value2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong value3() {
        return getBccpManifestId();
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
    public ModuleBccpManifestRecord value1(ULong value) {
        setModuleBccpManifestId(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value2(ULong value) {
        setModuleSetReleaseId(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value3(ULong value) {
        setBccpManifestId(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value4(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value6(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleBccpManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, ULong value5, ULong value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached ModuleBccpManifestRecord
     */
    public ModuleBccpManifestRecord() {
        super(ModuleBccpManifest.MODULE_BCCP_MANIFEST);
    }

    /**
     * Create a detached, initialised ModuleBccpManifestRecord
     */
    public ModuleBccpManifestRecord(ULong moduleBccpManifestId, ULong moduleSetReleaseId, ULong bccpManifestId, ULong moduleId, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleBccpManifest.MODULE_BCCP_MANIFEST);

        set(0, moduleBccpManifestId);
        set(1, moduleSetReleaseId);
        set(2, bccpManifestId);
        set(3, moduleId);
        set(4, createdBy);
        set(5, lastUpdatedBy);
        set(6, creationTimestamp);
        set(7, lastUpdateTimestamp);
    }
}
