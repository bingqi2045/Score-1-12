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
import org.oagi.score.repo.api.impl.jooq.entity.tables.ModuleAgencyIdListManifest;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ModuleAgencyIdListManifestRecord extends UpdatableRecordImpl<ModuleAgencyIdListManifestRecord> implements Record8<ULong, ULong, ULong, ULong, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.module_agency_id_list_manifest_id</code>.
     * Primary key.
     */
    public void setModuleAgencyIdListManifestId(ULong value) {
        set(0, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.module_agency_id_list_manifest_id</code>.
     * Primary key.
     */
    public ULong getModuleAgencyIdListManifestId() {
        return (ULong) get(0);
    }

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.module_set_release_id</code>. A
     * foreign key of the module set release record.
     */
    public void setModuleSetReleaseId(ULong value) {
        set(1, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.module_set_release_id</code>. A
     * foreign key of the module set release record.
     */
    public ULong getModuleSetReleaseId() {
        return (ULong) get(1);
    }

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.agency_id_list_manifest_id</code>.
     * A foreign key of the code list manifest record.
     */
    public void setAgencyIdListManifestId(ULong value) {
        set(2, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.agency_id_list_manifest_id</code>.
     * A foreign key of the code list manifest record.
     */
    public ULong getAgencyIdListManifestId() {
        return (ULong) get(2);
    }

    /**
     * Setter for <code>oagi.module_agency_id_list_manifest.module_id</code>.
     * This indicates a module.
     */
    public void setModuleId(ULong value) {
        set(3, value);
    }

    /**
     * Getter for <code>oagi.module_agency_id_list_manifest.module_id</code>.
     * This indicates a module.
     */
    public ULong getModuleId() {
        return (ULong) get(3);
    }

    /**
     * Setter for <code>oagi.module_agency_id_list_manifest.created_by</code>.
     * Foreign key to the APP_USER table. It indicates the user who created this
     * record.
     */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>oagi.module_agency_id_list_manifest.created_by</code>.
     * Foreign key to the APP_USER table. It indicates the user who created this
     * record.
     */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public void setLastUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.last_updated_by</code>. Foreign
     * key to the APP_USER table referring to the last user who updated the
     * record.
     */
    public String getLastUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public void setCreationTimestamp(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.creation_timestamp</code>. The
     * timestamp when the record was first created.
     */
    public LocalDateTime getCreationTimestamp() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for
     * <code>oagi.module_agency_id_list_manifest.last_update_timestamp</code>.
     * The timestamp when the record was last updated.
     */
    public void setLastUpdateTimestamp(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for
     * <code>oagi.module_agency_id_list_manifest.last_update_timestamp</code>.
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
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_SET_RELEASE_ID;
    }

    @Override
    public Field<ULong> field3() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID;
    }

    @Override
    public Field<ULong> field4() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.MODULE_ID;
    }

    @Override
    public Field<String> field5() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.CREATED_BY;
    }

    @Override
    public Field<String> field6() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.LAST_UPDATED_BY;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.CREATION_TIMESTAMP;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST.LAST_UPDATE_TIMESTAMP;
    }

    @Override
    public ULong component1() {
        return getModuleAgencyIdListManifestId();
    }

    @Override
    public ULong component2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong component3() {
        return getAgencyIdListManifestId();
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
        return getModuleAgencyIdListManifestId();
    }

    @Override
    public ULong value2() {
        return getModuleSetReleaseId();
    }

    @Override
    public ULong value3() {
        return getAgencyIdListManifestId();
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
    public ModuleAgencyIdListManifestRecord value1(ULong value) {
        setModuleAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value2(ULong value) {
        setModuleSetReleaseId(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value3(ULong value) {
        setAgencyIdListManifestId(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value4(ULong value) {
        setModuleId(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value6(String value) {
        setLastUpdatedBy(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value7(LocalDateTime value) {
        setCreationTimestamp(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord value8(LocalDateTime value) {
        setLastUpdateTimestamp(value);
        return this;
    }

    @Override
    public ModuleAgencyIdListManifestRecord values(ULong value1, ULong value2, ULong value3, ULong value4, String value5, String value6, LocalDateTime value7, LocalDateTime value8) {
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
     * Create a detached ModuleAgencyIdListManifestRecord
     */
    public ModuleAgencyIdListManifestRecord() {
        super(ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST);
    }

    /**
     * Create a detached, initialised ModuleAgencyIdListManifestRecord
     */
    public ModuleAgencyIdListManifestRecord(ULong moduleAgencyIdListManifestId, ULong moduleSetReleaseId, ULong agencyIdListManifestId, ULong moduleId, String createdBy, String lastUpdatedBy, LocalDateTime creationTimestamp, LocalDateTime lastUpdateTimestamp) {
        super(ModuleAgencyIdListManifest.MODULE_AGENCY_ID_LIST_MANIFEST);

        setModuleAgencyIdListManifestId(moduleAgencyIdListManifestId);
        setModuleSetReleaseId(moduleSetReleaseId);
        setAgencyIdListManifestId(agencyIdListManifestId);
        setModuleId(moduleId);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setCreationTimestamp(creationTimestamp);
        setLastUpdateTimestamp(lastUpdateTimestamp);
    }
}
