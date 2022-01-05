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
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleSetRelease;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleSetReleaseRecord extends UpdatableRecordImpl<ModuleSetReleaseRecord> implements Record8<ULong, ULong, ULong, Byte, ULong, ULong, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>oagi.module_set_release.module_set_release_id</code>.
     * Primary key.
     */
    public void setModuleSetReleaseId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.module_set_release_id</code>.
     * Primary key.
     */
    public ULong getModuleSetReleaseId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>oagi.module_set_release.module_set_id</code>. A foreign
     * key of the module set.
     */
    public void setModuleSetId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.module_set_id</code>. A foreign
     * key of the module set.
     */
    public ULong getModuleSetId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>oagi.module_set_release.release_id</code>. A foreign key
     * of the release.
     */
    public void setReleaseId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.release_id</code>. A foreign key
     * of the release.
     */
    public ULong getReleaseId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_set_release.is_default</code>. It would be a
     * default module set if this indicator is checked. Otherwise, it would be
     * an optional.
     */
    public void setIsDefault(Byte value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.is_default</code>. It would be a
     * default module set if this indicator is checked. Otherwise, it would be
     * an optional.
     */
    public Byte getIsDefault() {
        return (Byte) get(3);
    }

    /**
     * Setter for <code>oagi.module_set_release.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created this
     * MODULE_SET_RELEASE.
     */
    public void setCreatedBy(ULong value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.created_by</code>. Foreign key
     * to the APP_USER table. It indicates the user who created this
     * MODULE_SET_RELEASE.
     */
    public ULong getCreatedBy() {
        return (ULong) get(4);
    }

    /**
     * Setter for <code>oagi.module_set_release.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public void setLastUpdatedBy(ULong value) {
        set(5, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public ULong getLastUpdatedBy() {
        return (ULong) get(5);
    }

    /**
     * Setter for <code>oagi.module_set_release.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>oagi.module_set_release.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>oagi.module_set_release.last_update_timestamp</code>.
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
    public Row8<ULong, ULong, ULong, Byte, ULong, ULong, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<ULong, ULong, ULong, Byte, ULong, ULong, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return ModuleSetRelease.MODULE_SET_RELEASE.MODULE_SET_RELEASE_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ModuleSetRelease.MODULE_SET_RELEASE.MODULE_SET_ID;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleSetRelease.MODULE_SET_RELEASE.RELEASE_ID;
    }

    @Override
    public Field<Byte> field4() {
        return ModuleSetRelease.MODULE_SET_RELEASE.IS_DEFAULT;
    }

    @Override
    public Field<ULong> field5() {
        return ModuleSetRelease.MODULE_SET_RELEASE.CREATED_BY;
    }

    @Override
    public Field<ULong> field6() {
        return ModuleSetRelease.MODULE_SET_RELEASE.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleSetRelease.MODULE_SET_RELEASE.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleSetRelease.MODULE_SET_RELEASE.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong component2() {
        return getModuleSetId();
    }

    @Override
    public ULong component3() {
        return getReleaseId();
    }

    @Override
    public Byte component4() {
        return getIsDefault();
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
        return getModuleSetReleaseId();
    }

    @Override
    public ULong value2() {
        return getModuleSetId();
    }

    @Override
    public ULong value3() {
        return getReleaseId();
    }

    @Override
    public Byte value4() {
        return getIsDefault();
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
    public ModuleSetReleaseRecord value1(ULong value) {
        setModuleSetReleaseId(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value2(ULong value) {
        setModuleSetId(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value3(ULong value) {
        setReleaseId(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value4(Byte value) {
        setIsDefault(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value5(ULong value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value6(ULong value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleSetReleaseRecord values(ULong value1, ULong value2, ULong value3, Byte value4, ULong value5, ULong value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached ModuleSetReleaseRecord
     */
    public ModuleSetReleaseRecord() {
        super(ModuleSetRelease.MODULE_SET_RELEASE);
    }

    /**
     * Create a detached, initialised ModuleSetReleaseRecord
     */
    public ModuleSetReleaseRecord(ULong moduleSetReleaseId, ULong moduleSetId, ULong releaseId, Byte isDefault, ULong createdBy, ULong lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleSetRelease.MODULE_SET_RELEASE);

        setModuleSetReleaseId(moduleSetReleaseId);
        setModuleSetId(moduleSetId);
        setReleaseId(releaseId);
        setIsDefault(isDefault);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
