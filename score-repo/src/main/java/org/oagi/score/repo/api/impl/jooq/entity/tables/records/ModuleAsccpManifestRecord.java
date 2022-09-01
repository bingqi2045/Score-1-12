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
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleAsccpManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleAsccpManifestRecord extends UpdatableRecordImpl<ModuleAsccpManifestRecord> implements Record8<ULong, ULong, ULong, ULong, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for
     * <code>oagi.module_asccp_manifest.module_asccp_manifest_id</code>. Primary
     * key.
     */
    public void setModuleAsccpManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for
     * <code>oagi.module_asccp_manifest.module_asccp_manifest_id</code>. Primary
     * key.
     */
    public ULong getModuleAsccpManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.module_set_release_id</code>.
     * A foreign key of the module set release record.
     */
    public void setModuleSetReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.module_set_release_id</code>.
     * A foreign key of the module set release record.
     */
    public ULong getModuleSetReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.asccp_manifest_id</code>. A
     * foreign key of the asccp manifest record.
     */
    public void setAsccpManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.asccp_manifest_id</code>. A
     * foreign key of the asccp manifest record.
     */
    public ULong getAsccpManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.module_id</code>. This
     * indicates a module.
     */
    public void setModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.module_id</code>. This
     * indicates a module.
     */
    public ULong getModuleId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.created_by</code>. Foreign
     * key to the APP_USER table. It indicates the user who created this record.
     */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.created_by</code>. Foreign
     * key to the APP_USER table. It indicates the user who created this record.
     */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.last_updated_by</code>.
     * Foreign key to the APP_USER table referring to the last user who updated
     * the record.
     */
    public void setLastUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.last_updated_by</code>.
     * Foreign key to the APP_USER table referring to the last user who updated
     * the record.
     */
    public String getLastUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.creation_timestamp</code>.
     * The timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.creation_timestamp</code>.
     * The timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_asccp_manifest.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_asccp_manifest.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
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
    public Row8<ULong, ULong, ULong, ULong, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, ULong, ULong, ULong, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.MODULE_ASCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.MODULE_SET_RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.ASCCP_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<String> field5() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.CREATED_BY;
    }

    @Override
    public Field<String> field6() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleAsccpManifest.MODULE_ASCCP_MANIFEST.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleAsccpManifestId();
    }

    @Override
    public ULong component2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong component3() {
        return getAsccpManifestId();
    }

    @Override
    public ULong component4() {
        return getModuleId();
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
    public ULong value1() {
        return getModuleAsccpManifestId();
    }

    @Override
    public ULong value2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong value3() {
        return getAsccpManifestId();
    }

    @Override
    public ULong value4() {
        return getModuleId();
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
    public ModuleAsccpManifestRecord value1(ULong value) {
        setModuleAsccpManifestId(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value2(ULong value) {
        setModuleSetReleaseId(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value3(ULong value) {
        setAsccpManifestId(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value4(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value6(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleAsccpManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, String value5, String value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached ModuleAsccpManifestRecord
     */
    public ModuleAsccpManifestRecord() {
        super(ModuleAsccpManifest.MODULE_ASCCP_MANIFEST);
    }

    /**
     * Create a detached, initialised ModuleAsccpManifestRecord
     */
    public ModuleAsccpManifestRecord(ULong moduleAsccpManifestId, ULong moduleSetReleaseId, ULong asccpManifestId, ULong moduleId, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleAsccpManifest.MODULE_ASCCP_MANIFEST);

        setModuleAsccpManifestId(moduleAsccpManifestId);
        setModuleSetReleaseId(moduleSetReleaseId);
        setAsccpManifestId(asccpManifestId);
        setModuleId(moduleId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
